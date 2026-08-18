const state = {
  session: null,
  project: null,
  path: "",
  schema: null,
  provenance: null,
  selected: null,
  diagnostics: [],
  undo: [],
  redo: [],
  dirty: false,
  filter: "ALL",
  search: "",
  connecting: null,
  traces: [],
  trace: 0,
  step: 0,
  compareDocument: null,
};
let startMode = "prompt";
const $ = (s) => document.querySelector(s),
  $$ = (s) => [...document.querySelectorAll(s)],
  clone = (o) => JSON.parse(JSON.stringify(o));
const recoveryKey = () => `block-party-workbench:${state.path}`;

function workingPath(path) {
  if (!path || /^[a-zA-Z]:[\\/]/.test(path) || path.startsWith("/")) {
    return path;
  }
  const root = (state.session?.workingDirectory || "").replace(/[\\/]+$/, "");
  const separator = root.includes("\\") ? "\\" : "/";
  return root
    ? `${root}${separator}${path.replaceAll(/[\\/]/g, separator)}`
    : path;
}

async function api(path, body) {
  const options =
    body === undefined
      ? {}
      : {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(body),
        };
  const response = await fetch("/api/" + path, options);
  const result = await response.json();
  if (!response.ok) throw new Error(result.error || "Request failed");
  return result;
}
async function loadProject() {
  try {
    const [data, schema, provenance] = await Promise.all([
      api(`project?document=${encodeURIComponent(state.session?.activeDocument || "")}`),
      api("schema"),
      api("provenance"),
    ]);
    state.project = data.project;
    state.schema = schema;
    state.provenance = provenance;
    state.path = data.path;
    state.undo = [];
    state.redo = [];
    state.selected = null;
    state.dirty = false;
    normalize();
    const recovered = localStorage.getItem(recoveryKey());
    if (
      recovered &&
      confirm("Recover unsaved work from the previous workbench session?")
    ) {
      state.project = JSON.parse(recovered);
      normalize();
      state.dirty = true;
    }
    $("#project-path").textContent = data.path;
    $("#export-path").value = data.defaultExportPath || "";
    $("#export-path").dataset.standard = data.defaultExportPath || "";
    $("#export-path").dataset.live = data.liveResourcesPath || "";
    $("#export-live-resources").checked = false;
    $("#export-path").disabled = false;
    $("#dirty").textContent = "Saved";
    $("#save").disabled = true;
    restoreScenario();
    document.body.classList.remove("start-mode");
    $("#start-screen").hidden = true;
    renderWorkspaceChrome();
    render();
    await validate();
  } catch (e) {
    toast(e.message, true);
  }
}

async function bootstrap() {
  try {
    state.session = await api("session");
    if (state.session.projectOpen) {
      await loadProject();
    } else if (state.session.userState?.lastSolution) {
      state.session = await api("solution/open", {
        path: state.session.userState.lastSolution,
      });
      if (state.session.projectOpen) await loadProject();
      else showStartScreen();
    } else {
      showStartScreen();
    }
  } catch (error) {
    toast(error.message, true);
  }
}

function showStartScreen() {
  document.body.classList.add("start-mode");
  $("#start-screen").hidden = false;
  $("#start-create-panel").hidden = true;
  $("#start-open-panel").hidden = true;
  $("#start-solution-panel").hidden = true;
  $("#start-working-directory").textContent = state.session?.workingDirectory
    ? `Working directory: ${state.session.workingDirectory}`
    : "";
  renderStartSolution();
  renderRecents();
}

function renderStartSolution() {
  const panel = $("#start-active-solution");
  panel.hidden = !state.session?.solutionOpen;
  if (!state.session?.solutionOpen) return;
  $("#start-active-solution-name").textContent = state.session.solution.name;
  $("#start-active-solution-path").textContent = state.session.solutionPath;
  const projects = state.session.solution.projects || [];
  $("#start-solution-project-list").innerHTML = projects.length
    ? projects.map(item => `<button type="button" data-start-solution-project="${attr(item.path)}"><strong>${esc(item.id)}</strong><small>${esc(item.group || "Projects")}${item.missing ? " · missing" : ""}</small></button>`).join("")
    : '<p class="hint">This solution is empty. Create a generated or blank project below to add its first scene pack.</p>';
  $$('[data-start-solution-project]').forEach(button => button.onclick = () => openPack(button.dataset.startSolutionProject));
}

function recentPacks() { return state.session?.userState?.recentProjects || []; }

function renderRecents() {
  const root = $("#recent-packs");
  const projects = recentPacks();
  const solutions = state.session?.userState?.recentSolutions || [];
  const generated = state.session?.recentGenerations || [];
  const pinnedProjects = state.session?.userState?.pinnedProjects || [];
  const pinnedSolutions = state.session?.userState?.pinnedSolutions || [];
  const pinned = [...pinnedSolutions.map(path => ({ path, title: solutions.find(item => item.path === path)?.title || path, solution:true, pinned:true, opened:Number.MAX_SAFE_INTEGER })), ...pinnedProjects.map(path => ({ path, title: projects.find(item => item.path === path)?.title || path, pinned:true, opened:Number.MAX_SAFE_INTEGER }))];
  const recents = [...pinned, ...solutions.map((item) => ({ ...item, solution: true })), ...projects, ...generated.map(item => ({ ...item, generated: true, opened: 0 }))].filter((item,index,all) => all.findIndex(other => other.path === item.path) === index).sort((a,b) => b.opened-a.opened).slice(0,16);
  if (!recents.length) {
    root.innerHTML =
      '<div class="recent-empty">Packs you open will appear here.</div>';
    return;
  }
  root.innerHTML = recents
    .map(
      (item, index) =>
        `<div class="recent-pack"><button type="button" data-recent-open="${index}"><strong>${item.pinned ? "★ " : item.solution ? "◆ " : item.generated ? "✦ " : ""}${esc(item.title)}</strong><small>${item.generated ? "Recently generated · " : ""}${esc(item.path)}</small></button></div>`,
    )
    .join("");
  $$("[data-recent-open]").forEach(
    (button) =>
      (button.onclick = () =>
        (recents[Number(button.dataset.recentOpen)].solution ? openSolution : openPack)(recents[Number(button.dataset.recentOpen)].path)),
  );
}

async function openSolution(path) {
  try {
    state.session = await api("solution/open", { path: workingPath(path) });
    if (state.session.projectOpen) await loadProject(); else { showStartScreen(); renderRecents(); }
  } catch (error) { toast(error.message, true); }
}

async function createSolution() {
  const path = workingPath($("#start-solution-path").value.trim());
  const name = $("#start-solution-name").value.trim();
  if (!path || !name) return toast("Solution path and name are required.", true);
  try { state.session = await api("solution/new", { path, name }); showStartScreen(); renderRecents(); }
  catch (error) { toast(error.message, true); }
}

function renderWorkspaceChrome() {
  const tabs = $("#document-tabs");
  tabs.innerHTML = (state.session?.documents || []).map(document => `<button class="document-tab ${document.id === state.session.activeDocument ? "active" : ""}" data-document="${attr(document.id)}"><span class="tab-title">${esc(document.title)}</span><span class="tab-close" data-close-document="${attr(document.id)}">×</span></button>`).join("");
  $$('[data-document]').forEach(button => button.onclick = async event => {
    if (event.target.dataset.closeDocument) return;
    if (state.dirty && !confirm("Switch projects with unsaved changes?")) return;
    state.session = await api("document/activate", { document: button.dataset.document }); await loadProject();
  });
  $$('[data-close-document]').forEach(button => button.onclick = async event => {
    event.stopPropagation();
    if (button.dataset.closeDocument === state.session.activeDocument && state.dirty && !confirm("Close this project with unsaved changes?")) return;
    state.session = await api("document/close", { document: button.dataset.closeDocument });
    if (state.session.projectOpen) await loadProject(); else showStartScreen();
  });
  const explorer = $("#solution-explorer");
  explorer.hidden = !state.session?.solutionOpen;
  if (!state.session?.solutionOpen) return;
  $("#solution-name").textContent = state.session.solution.name;
  const solutionPath = state.session.solution.path;
  const solutionPinned = (state.session.userState?.pinnedSolutions || []).includes(solutionPath);
  $("#pin-solution").classList.toggle("active", solutionPinned);
  $("#pin-solution").textContent = solutionPinned ? "★" : "☆";
  $("#pin-solution").onclick = async () => {
    state.session = await api("state/pin", {
      kind: "solution",
      path: solutionPath,
      pinned: !$("#pin-solution").classList.contains("active"),
    });
    renderWorkspaceChrome();
  };
  const groups = (state.session.solution.projects || []).reduce((result, item) => { (result[item.group || "Projects"] ??= []).push(item); return result; }, {});
  const pinnedPaths = state.session?.userState?.pinnedProjects || [];
  $("#solution-projects").innerHTML = Object.entries(groups).map(([group, projects]) => `<div class="solution-group"><b>${esc(group)}</b>${projects.map(item => `<div class="solution-project ${item.missing ? "missing" : ""}"><button data-solution-open="${attr(item.path)}">${esc(item.id)}</button><button class="pin ${pinnedPaths.includes(item.path) ? "active" : ""}" data-pin-project="${attr(item.path)}" title="Pin">${pinnedPaths.includes(item.path) ? "★" : "☆"}</button><button data-split-project="${attr(item.path)}" title="Open side by side">◫</button></div>`).join("")}</div>`).join("");
  $$('[data-solution-open]').forEach(button => button.onclick = () => openPack(button.dataset.solutionOpen));
  $$('[data-split-project]').forEach(button => button.onclick = () => openCompare(button.dataset.splitProject));
  $$('[data-pin-project]').forEach(button => button.onclick = async () => { const pinned = !button.classList.contains("active"); state.session = await api("state/pin", { kind:"project", path:button.dataset.pinProject, pinned }); renderWorkspaceChrome(); });
}

async function openCompare(path) {
  try {
    state.session = await api("open", { path });
    const id = state.session.activeDocument;
    const data = await api(`project?document=${encodeURIComponent(id)}`);
    state.compareDocument = id;
    $("#compare-title").textContent = data.project.pack?.title || data.project.pack?.id;
    $("#compare-content").innerHTML = (data.project.nodes || []).map(node => `<article class="compare-card"><small>${esc(node.type)} · ${esc(node.id)}</small><strong>${esc(node.title || node.id)}</strong>${node.text ? `<p>${esc(node.text)}</p>` : ""}</article>`).join("");
    $("#compare-pane").hidden = false;
    await api("document/activate", { document: state.session.documents.find(item => item.path === state.path)?.id || state.session.documents[0].id });
    state.session = await api("session"); renderWorkspaceChrome();
  } catch (error) { toast(error.message, true); }
}

function slug(value) {
  return value
    .toLowerCase()
    .trim()
    .replace(/[^a-z0-9]+/g, "_")
    .replace(/^_+|_+$/g, "");
}

function openStartPanel(mode) {
  startMode = mode;
  $("#start-create-panel").hidden = mode === "open" || mode === "solution";
  $("#start-open-panel").hidden = mode !== "open";
  $("#start-solution-panel").hidden = mode !== "solution";
  if (mode !== "open" && mode !== "solution") {
    const prompt = mode === "prompt";
    $("#start-prompt-fields").hidden = !prompt;
    $("#start-create-kicker").textContent = prompt
      ? "PROMPT TO PACK"
      : "NEW PACK";
    $("#start-create-heading").textContent = prompt
      ? "Describe the conversation you want"
      : "Create a minimal card graph";
    $("#start-create").textContent = prompt
      ? "Create and generate"
      : "Create blank pack";
    $("#start-title").focus();
  } else if (mode === "open") {
    $("#start-open-path").focus();
  } else {
    $("#start-solution-path").focus();
  }
}

async function openPack(path) {
  try {
    state.session = await api("open", { path });
    await loadProject();
  } catch (error) {
    toast(error.message, true);
  }
}

async function createPack() {
  const title = $("#start-title").value.trim();
  const id = slug($("#start-id").value || title);
  if (!title || !id) {
    toast("Pack title and id are required.", true);
    return;
  }
  if (startMode === "prompt" && !$("#start-prompt").value.trim()) {
    toast("Describe the conversation you want to generate.", true);
    return;
  }
  try {
    state.session = await api("new", {
      id,
      title,
      path: $("#start-source").value.trim(),
      addToSolution: startMode !== "prompt",
    });
    await loadProject();
    if (startMode === "prompt") {
      openGenerationStudio();
      $("#gen-id").value = id;
      $("#gen-title").value = title;
      $("#gen-prompt").value = $("#start-prompt").value.trim();
      $("#gen-characters").value = $("#start-characters").value.trim();
      $("#gen-subject").value = $("#start-subject").value.trim();
      $("#gen-documents").value = $("#start-documents")
        .value.split(",")
        .map((value) => value.trim())
        .filter(Boolean)
        .join("\n");
      $("#gen-output").value = state.path.replace(
        /[\\/]project\.json$/,
        "\\generated",
      );
    } else {
      openProjectSettings();
    }
  } catch (error) {
    toast(error.message, true);
  }
}

async function returnToStart() {
  if (state.dirty && !confirm("Leave this pack with unsaved changes?")) return;
  try {
    state.session = await api("close", {});
    state.project = null;
    state.path = "";
    showStartScreen();
  } catch (error) {
    toast(error.message, true);
  }
}
function normalize() {
  state.project.nodes ??= [];
  state.project.state ??= [];
  state.project.contract ??= { requires: [], provides: [], outcomes: [] };
  state.project.contract.requires ??= [];
  state.project.contract.provides ??= [];
  state.project.contract.outcomes ??= [];
  for (const [i, n] of state.project.nodes.entries()) {
    n.conditions ??= [];
    n.actions ??= [];
    n.responses ??= [];
    for (const r of n.responses) r.actions ??= [];
    n.editor ??= { x: 80 + (i % 3) * 290, y: 80 + Math.floor(i / 3) * 190 };
  }
  normalizeEditorPositions(state.project.nodes);
}
function normalizeEditorPositions(nodes) {
  if (!nodes.length) return false;
  for (const [i, node] of nodes.entries()) {
    node.editor ??= { x: 80 + (i % 3) * 290, y: 80 + Math.floor(i / 3) * 190 };
    if (!Number.isFinite(node.editor.x)) node.editor.x = 80 + (i % 3) * 290;
    if (!Number.isFinite(node.editor.y)) node.editor.y = 80 + Math.floor(i / 3) * 190;
  }
  const minX = Math.min(...nodes.map((node) => node.editor.x));
  const minY = Math.min(...nodes.map((node) => node.editor.y));
  const shiftX = Math.max(0, 80 - minX);
  const shiftY = Math.max(0, 80 - minY);
  if (!shiftX && !shiftY) return false;
  for (const node of nodes) {
    node.editor.x += shiftX;
    node.editor.y += shiftY;
  }
  return true;
}
function snapshot() {
  state.undo.push(JSON.stringify(state.project));
  if (state.undo.length > 100) state.undo.shift();
  state.redo = [];
}
let recoveryTimer;
function changed(renderAll = true) {
  state.dirty = true;
  $("#dirty").textContent = "Unsaved";
  $("#save").disabled = false;
  clearTimeout(recoveryTimer);
  recoveryTimer = setTimeout(
    () => localStorage.setItem(recoveryKey(), JSON.stringify(state.project)),
    300,
  );
  if (renderAll) render();
  scheduleValidate();
}
function restore(stack, to) {
  if (!stack.length) return;
  to.push(JSON.stringify(state.project));
  state.project = JSON.parse(stack.pop());
  normalize();
  if (!state.project.nodes.some((n) => n.id === state.selected))
    state.selected = null;
  changed();
}

function render() {
  renderHeader();
  renderOutline();
  renderCanvas();
  renderInspector();
  $("#undo").disabled = !state.undo.length;
  $("#redo").disabled = !state.redo.length;
  $("#connect-card").disabled = !state.selected;
}
function renderHeader() {
  const p = state.project;
  $("#pack-title").textContent = p.pack?.title || p.pack?.id || "Conversation";
  $("#pack-meta").textContent =
    `${p.nodes.length} cards · ${p.target?.mod || "Block Party"}`;
  $("#card-count").textContent = p.nodes.length;
}
function visible(n) {
  const q = state.search.toLowerCase();
  return (
    (state.filter === "ALL" || n.type === state.filter) &&
    (!q ||
      [n.id, n.title, n.text, n.trigger, n.ending]
        .join(" ")
        .toLowerCase()
        .includes(q))
  );
}
function blockingReviewFindings() {
  return (state.provenance?.review?.findings || []).filter(
    (finding) => (finding.severity || "").toUpperCase() === "HIGH",
  );
}
function renderOutline() {
  const root = $("#outline");
  root.innerHTML = "";
  for (const n of state.project.nodes.filter(visible)) {
    const b = document.createElement("button");
    b.className = "outline-item" + (n.id === state.selected ? " active" : "");
    b.innerHTML = `<i class="mini-type ${n.type}"></i><span>${esc(n.title || n.id)}</span>`;
    b.onclick = () => select(n.id, true);
    root.append(b);
  }
}
function renderCanvas() {
  const cards = $("#cards");
  cards.innerHTML = "";
  $(".workspace").classList.toggle("connecting", !!state.connecting);
  const visibleNodes = state.project.nodes.filter(visible);
  const shown = new Set(visibleNodes.map((n) => n.id));
  const canvas = $("#canvas");
  canvas.style.width = `${Math.max(1800, ...visibleNodes.map((n) => n.editor.x + 310))}px`;
  canvas.style.height = `${Math.max(1200, ...visibleNodes.map((n) => n.editor.y + 220))}px`;
  for (const n of visibleNodes) {
    const card = document.createElement("article");
    card.className = `node-card ${n.type}${n.id === state.selected ? " selected" : ""}${state.diagnostics.some((d) => d.node === n.id && d.severity === "ERROR") || blockingReviewFindings().some((finding) => finding.node === n.id) ? " has-error" : ""}${state.connecting === n.id ? " connect-source" : state.connecting ? " connect-target" : ""}`;
    card.style.left = n.editor.x + "px";
    card.style.top = n.editor.y + "px";
    card.dataset.id = n.id;
    card.innerHTML = `<div class="card-top"><i class="card-type"></i><span class="card-title">${esc(n.title || n.id)}</span><span class="card-id">${esc(n.id)}</span></div><div class="card-text">${esc((n.text || n.ending || "No dialogue").slice(0, 130))}</div><div class="card-footer"><span>${n.conditions.length} conditions · ${n.actions.length} actions</span><span class="port">${targets(n).length} →</span></div>`;
    card.onpointerdown = (e) => dragStart(e, n, card);
    card.onclick = (e) => {
      e.stopPropagation();
      state.connecting && state.connecting !== n.id
        ? finishConnection(n.id)
        : select(n.id);
    };
    cards.append(card);
  }
  drawEdges(shown);
}
function targets(n) {
  return [
    ...(n.responses || []).map((r) => ({
      id: r.target,
      label: r.label,
      type: r.transition,
    })),
    ...(n.next ? [{ id: n.next, label: "next", type: "EXTERNAL_EVENT" }] : []),
  ];
}
function drawEdges(shown) {
  const svg = $("#edges");
  svg.innerHTML = "";
  for (const n of state.project.nodes) {
    if (!shown.has(n.id)) continue;
    for (const target of targets(n)) {
      const dest = state.project.nodes.find((x) => x.id === target.id);
      if (!dest || !shown.has(dest.id)) continue;
      const x1 = n.editor.x + 230,
        y1 = n.editor.y + 60,
        x2 = dest.editor.x,
        y2 = dest.editor.y + 60,
        mid = (x1 + x2) / 2;
      const path = document.createElementNS(
        "http://www.w3.org/2000/svg",
        "path",
      );
      path.setAttribute(
        "d",
        `M ${x1} ${y1} C ${mid} ${y1}, ${mid} ${y2}, ${x2} ${y2}`,
      );
      path.setAttribute(
        "class",
        "edge " + (target.type === "IMMEDIATE" ? "" : "later"),
      );
      svg.append(path);
    }
  }
}
function dragStart(e, node, card) {
  if (e.button !== 0 || state.connecting) return;
  select(node.id);
  const start = {
    x: e.clientX,
    y: e.clientY,
    nx: node.editor.x,
    ny: node.editor.y,
  };
  let moved = false;
  card.setPointerCapture(e.pointerId);
  card.onpointermove = (ev) => {
    if (Math.abs(ev.clientX - start.x) + Math.abs(ev.clientY - start.y) > 4)
      moved = true;
    if (!moved) return;
    node.editor.x = Math.max(0, start.nx + ev.clientX - start.x);
    node.editor.y = Math.max(0, start.ny + ev.clientY - start.y);
    card.style.left = node.editor.x + "px";
    card.style.top = node.editor.y + "px";
    drawEdges(new Set(state.project.nodes.filter(visible).map((n) => n.id)));
  };
  card.onpointerup = () => {
    card.onpointermove = null;
    if (moved) {
      state.undo.push(
        JSON.stringify({
          ...state.project,
          nodes: state.project.nodes.map((n) =>
            n.id === node.id
              ? { ...n, editor: { x: start.nx, y: start.ny } }
              : n,
          ),
        }),
      );
      state.redo = [];
      changed(false);
    }
  };
}
function select(id, scroll = false) {
  state.selected = id;
  render();
  if (scroll) {
    const n = selected();
    $("#viewport").scrollTo({
      left: Math.max(0, n.editor.x - 100),
      top: Math.max(0, n.editor.y - 80),
      behavior: "smooth",
    });
  }
}
function startConnection() {
  if (!state.selected) return;
  state.connecting = state.connecting ? null : state.selected;
  $("#connect-card").classList.toggle("active", !!state.connecting);
  renderCanvas();
  if (state.connecting) toast("Select a destination card");
}
function finishConnection(target) {
  const source = state.project.nodes.find((n) => n.id === state.connecting);
  snapshot();
  if (source.type === "DIALOGUE") {
    const requested = prompt(
      "Transition type: IMMEDIATE, LATER_INTERACTION, EXTERNAL_EVENT, or PACK_EXIT",
      "IMMEDIATE",
    );
    const transition = state.schema.enums.transition.includes(requested)
      ? requested
      : "IMMEDIATE";
    source.responses.push({
      cue: "chat_bubble",
      label: "Continue",
      target,
      transition,
      actions: [],
    });
  } else source.next = target;
  state.connecting = null;
  $("#connect-card").classList.remove("active");
  changed();
  select(target);
}
function arrange() {
  snapshot();
  const nodes = state.project.nodes;
  const depth = new Map([[state.project.entry, 0]]);
  for (let pass = 0; pass < nodes.length; pass++)
    for (const n of nodes)
      if (depth.has(n.id))
        for (const t of targets(n))
          if (!depth.has(t.id)) depth.set(t.id, depth.get(n.id) + 1);
  const rows = {};
  for (const n of nodes) {
    const d = depth.get(n.id) ?? 0;
    rows[d] ??= [];
    rows[d].push(n);
  }
  for (const [d, row] of Object.entries(rows))
    row.forEach(
      (n, i) => (n.editor = { x: 70 + Number(d) * 290, y: 70 + i * 180 }),
    );
  changed();
}

function renderInspector() {
  const n = selected();
  $("#empty-inspector").hidden = !!n;
  $("#card-form").hidden = !n;
  if (!n) return;
  const f = $("#card-form");
  f.elements.trigger.innerHTML =
    '<option value="">Default (right click)</option>' +
    (state.schema.enums.trigger || [])
      .map((value) => `<option value="${attr(value)}">${esc(value.replaceAll("_", " "))}</option>`)
      .join("");
  for (const key of ["emotion", "animation"]) {
    f.elements[key].innerHTML =
      '<option value="">No speaker override</option>' +
      (state.schema.enums[key] || [])
        .map((value) => `<option value="${attr(value)}">${esc(value.replaceAll("_", " "))}</option>`)
        .join("");
    f.elements[key].value = n.speaker?.[key]?.toUpperCase() ?? "";
  }
  for (const key of [
    "id",
    "title",
    "type",
    "trigger",
    "text",
    "next",
    "ending",
  ])
    f.elements[key].value = n[key] ?? "";
  f.elements.selectionGroup.value = n.selection?.group || "";
  f.elements.selectionWeight.value = n.selection?.weight || 1;
  f.elements.selectionCooldownTicks.value = n.selection?.cooldownTicks || 0;
  $("#type-badge").textContent = n.type;
  $("#response-count").textContent = n.responses.length;
  $("#condition-count").textContent = n.conditions.length;
  $("#action-count").textContent = n.actions.length;
  $("#node-ids").innerHTML = state.project.nodes
    .filter((x) => x.id !== n.id)
    .map((x) => `<option value="${attr(x.id)}">`)
    .join("");
  $("#state-ids").innerHTML = state.project.state
    .map((x) => `<option value="${attr(x.id)}">`)
    .join("");
  $("#cue-ids").innerHTML = state.schema.cues
    .map((x) => `<option value="${attr(x)}">`)
    .join("");
  const intention = state.provenance?.intentions?.scenes?.find(
    (x) => x.node === n.id,
  );
  const intentionBox = $("#card-intention");
  intentionBox.hidden = !intention;
  intentionBox.innerHTML = intention
    ? `<b>Generated intention</b><br>${esc(intention.speakerObjective)} · ${esc(intention.emotionalState)}<br><span>${esc((intention.continuity || []).join(" · "))}</span>`
    : "";
  renderResponses(n);
  renderPrimitiveList(
    $("#conditions-editor"),
    n.conditions,
    "condition",
    (list) => editSelected((x) => (x.conditions = list)),
  );
  renderPrimitiveList($("#actions-editor"), n.actions, "action", (list) =>
    editSelected((x) => (x.actions = list)),
  );
}
function selected() {
  return state.project.nodes.find((n) => n.id === state.selected);
}
function editSelected(fn) {
  const n = selected();
  if (!n) return;
  snapshot();
  fn(n);
  changed();
}
function renderResponses(n) {
  const root = $("#responses");
  root.innerHTML = "";
  const add = $("#add-response");
  add.disabled = n.responses.length >= state.schema.maximumResponses;
  add.title = add.disabled
    ? `The dialogue UI supports at most ${state.schema.maximumResponses} responses.`
    : "";
  n.responses.forEach((r, i) => {
    const box = document.createElement("div");
    box.className = "response";
    box.innerHTML = `<button type="button" class="response-remove" aria-label="Remove response">×</button><div class="response-grid"><label>Label<input data-key="label" value="${attr(r.label || "")}"></label><label>Cue<input data-key="cue" list="cue-ids" value="${attr(r.cue || "")}"></label><label>Target<input data-key="target" list="node-ids" value="${attr(r.target || "")}"></label><label>Transition<select data-key="transition">${state.schema.enums.transition.map((v) => `<option${r.transition === v ? " selected" : ""}>${v}</option>`).join("")}</select></label></div><details><summary>Response actions <b>${r.actions.length}</b></summary><div class="response-actions"></div><button type="button" class="add-response-action">+ Add action</button></details>`;
    box.querySelector(".response-remove").onclick = () =>
      editSelected((x) => x.responses.splice(i, 1));
    box
      .querySelectorAll("[data-key]")
      .forEach(
        (input) =>
          (input.onchange = () =>
            editSelected(
              (x) => (x.responses[i][input.dataset.key] = input.value),
            )),
      );
    renderPrimitiveList(
      box.querySelector(".response-actions"),
      r.actions,
      "action",
      (list) => editSelected((x) => (x.responses[i].actions = list)),
    );
    box.querySelector(".add-response-action").onclick = () =>
      editSelected((x) =>
        x.responses[i].actions.push({
          type: "SET_COOKIE",
          scope: "PLAYER",
          state: "",
          value: "true",
        }),
      );
    root.append(box);
  });
}

function renderPrimitiveList(root, list, kind, commit) {
  root.innerHTML = "";
  const types =
    kind === "condition"
      ? state.schema.conditionTypes
      : state.schema.actionTypes;
  const definitions =
    kind === "condition" ? state.schema.conditions : state.schema.actions;
  list.forEach((primitive, index) => {
    const box = document.createElement("div");
    box.className = "primitive";
    box.innerHTML = `<div class="primitive-head"><select class="primitive-type">${types.map((t) => `<option${primitive.type === t ? " selected" : ""}>${t}</option>`).join("")}</select><button type="button" class="primitive-remove">×</button></div><div class="primitive-fields"></div>`;
    box.querySelector(".primitive-remove").onclick = () => {
      const next = clone(list);
      next.splice(index, 1);
      commit(next);
    };
    box.querySelector(".primitive-type").onchange = (e) => {
      const next = clone(list);
      next[index] = { type: e.target.value };
      commit(next);
    };
    const fields = box.querySelector(".primitive-fields");
    for (const field of definitions[primitive.type] || [])
      fields.append(
        primitiveField(field, primitive[field], kind, (value) => {
          const next = clone(list);
          if (value === undefined) delete next[index][field];
          else next[index][field] = value;
          commit(next);
        }, primitive.type),
      );
    root.append(box);
  });
}
function primitiveField(name, value, kind, commit, primitiveType) {
  const label = document.createElement("label");
  label.textContent = words(name);
  if (name === "filter") {
    const filter = value && typeof value === "object" ? clone(value) : { type: "block_party:always" };
    const selectedFilterType = filter.type || "block_party:always";
    delete filter.type;
    const type = document.createElement("select");
    type.innerHTML = (state.schema.sceneFilters || [])
      .map((entry) => `<option value="${attr(entry)}"${selectedFilterType === entry ? " selected" : ""}>${esc(entry)}</option>`)
      .join("");
    const fieldEditor = document.createElement("div");
    fieldEditor.className = "scene-filter-fields";
    const advanced = document.createElement("details");
    const summary = document.createElement("summary");
    summary.textContent = "Advanced JSON";
    const payload = document.createElement("textarea");
    advanced.append(summary, payload);
    const hint = document.createElement("small");
    const filterPath = () => type.value.replace(/^.*:/, "");
    const editableFields = () => state.schema.sceneFilterFields?.[filterPath()] || [];
    const syncPayload = () => {
      const fields = clone(filter);
      delete fields.type;
      payload.value = JSON.stringify(fields, null, 2);
    };
    const save = () => commit({ type: type.value, ...clone(filter) });
    const renderFields = () => {
      fieldEditor.innerHTML = "";
      const path = filterPath();
      const expected = editableFields();
      const valueChoices = state.schema.sceneFilterEnums?.[path] || [];
      for (const field of expected) {
        const row = document.createElement("label");
        row.textContent = words(field);
        let control;
        const choices = field === "value" ? valueChoices : state.schema.sceneFilterFieldEnums?.[field] || [];
        if (choices.length) {
          control = document.createElement("select");
          control.innerHTML = `<option value="">Choose…</option>${choices.map((choice) => `<option value="${attr(choice)}"${filter[field] === choice ? " selected" : ""}>${esc(choice)}</option>`).join("")}`;
        } else if (field === "not") {
          control = document.createElement("input");
          control.type = "checkbox";
          control.checked = !!filter[field];
        } else if ((["value", "start", "end", "radius"].includes(field)
          && ["if_time", "altitude", "light_level", "distance_to_location", "distance_to_assignment", "seconds_since_routine"].includes(path))
          || field === "radius") {
          control = document.createElement("input");
          control.type = "number";
          control.step = "any";
          control.value = filter[field] ?? 0;
        } else {
          control = document.createElement("input");
          control.value = filter[field] ?? "";
          if (field === "block") control.placeholder = "minecraft:block or #namespace:tag";
          if (field === "value" && ["dimension", "biome", "location_dimension"].includes(path)) control.placeholder = "namespace:resource";
        }
        control.onchange = () => {
          const next = control.type === "checkbox" ? control.checked
            : control.type === "number" ? Number(control.value)
              : control.value || undefined;
          if (next === undefined || next === false && field === "not") delete filter[field];
          else filter[field] = next;
          syncPayload();
          save();
        };
        row.append(control);
        fieldEditor.append(row);
      }
      hint.textContent = expected.length
        ? "This filter is fully editable here; Advanced JSON preserves optional fields."
        : "This filter has no required fields. Use Advanced JSON for optional legacy fields.";
      syncPayload();
    };
    const updateType = () => {
      const path = type.value.replace(/^.*:/, "");
      const choices = state.schema.sceneFilterEnums?.[path] || [];
      for (const key of Object.keys(filter)) delete filter[key];
      if (choices.length) filter.value = choices[0];
      renderFields();
      save();
    };
    type.onchange = updateType;
    payload.onchange = () => {
      try {
        const parsed = JSON.parse(payload.value || "{}");
        for (const key of Object.keys(filter)) delete filter[key];
        Object.assign(filter, parsed);
        renderFields();
        save();
      } catch (e) {
        toast("Scene filter fields must be valid JSON.", true);
      }
    };
    renderFields();
    label.append(type, fieldEditor, advanced, hint);
    return label;
  }
  let input;
  const contextualEnum =
    primitiveType === "REMEMBER_LOCATION" && name === "source"
      ? "locationSource"
      : primitiveType === "ASSIGN_TARGET" && name === "target"
        ? "assignmentTarget"
        : name;
  const enumValues = state.schema.enums[contextualEnum];
  if (enumValues) {
    input = document.createElement("select");
    input.innerHTML =
      `<option value="">Choose…</option>` +
      enumValues
        .map((v) => `<option${value === v ? " selected" : ""}>${v}</option>`)
        .join("");
  } else if (["not", "canChangeDimension", "triggerScene"].includes(name)) {
    input = document.createElement("input");
    input.type = "checkbox";
    input.checked = !!value;
  } else if (
    ["count", "amount", "ticks", "minTicks", "maxTicks", "searchRadius", "verticalRadius", "minGameDays", "speed", "arrivalRadius", "timeoutTicks"].includes(name) ||
    (name === "value" && kind === "condition")
  ) {
    input = document.createElement("input");
    input.type = "number";
    if (["speed", "arrivalRadius"].includes(name)) input.step = "any";
    input.value = value ?? 0;
  } else if (name === "raw" || name === "filter") {
    input = document.createElement("textarea");
    input.value = JSON.stringify(value ?? (name === "filter" ? { type: "block_party:always" } : {}), null, 2);
  } else {
    input = document.createElement("input");
    input.value = value ?? "";
    if (name === "state") input.setAttribute("list", "state-ids");
  }
  input.onchange = () => {
    if (input.type === "checkbox") commit(input.checked);
    else if (input.type === "number") commit(Number(input.value));
    else if (name === "raw" || name === "filter") {
      try {
        commit(JSON.parse(input.value));
      } catch (e) {
        toast(`${words(name)} must be valid JSON.`, true);
      }
    } else commit(input.value || undefined);
  };
  label.append(input);
  return label;
}

function openProjectSettings() {
  const p = state.project;
  $("#setting-pack-id").value = p.pack?.id || "";
  $("#setting-title").value = p.pack?.title || "";
  $("#setting-namespace").value = p.pack?.namespace || "";
  $("#setting-entry").value = p.entry || "";
  $("#setting-format").value = p.pack?.format || 1;
  $("#setting-raw").checked = !!p.allowRawMechanics;
  $("#setting-requires").value = (p.contract?.requires || [])
    .map((x) => x.id)
    .join(", ");
  $("#setting-provides").value = (p.contract?.provides || [])
    .map((x) => x.id)
    .join(", ");
  $("#setting-outcomes").value = (p.contract?.outcomes || []).join("\n");
  renderStateRows(clone(p.state).map((s) => ({ ...s, __originalId: s.id })));
  $("#project-dialog").showModal();
}
let draftStates = [];
function renderStateRows(states) {
  draftStates = states;
  const root = $("#state-editor");
  root.innerHTML = "";
  states.forEach((s, i) => {
    const initial =
      s.type === "COUNTER"
        ? `<label>Initial<input data-k="initialCounter" type="number" value="${s.initialCounter ?? 0}"></label>`
        : `<label class="check-label"><input data-k="initialCookie" type="checkbox"${s.initialCookie ? " checked" : ""}> Initially set</label>`;
    const row = document.createElement("div");
    row.className = "state-row";
    row.innerHTML = `<label>Id<input data-k="id" value="${attr(s.id || "")}"></label><label>Type<select data-k="type">${state.schema.enums.stateType.map((v) => `<option${s.type === v ? " selected" : ""}>${v}</option>`).join("")}</select></label><label>Scope<select data-k="scope">${state.schema.enums.scope.map((v) => `<option${s.scope === v ? " selected" : ""}>${v}</option>`).join("")}</select></label>${initial}<label>Minimum<input data-k="minimum" type="number" value="${s.minimum ?? ""}"></label><label>Maximum<input data-k="maximum" type="number" value="${s.maximum ?? ""}"></label><button type="button">×</button>`;
    row.querySelectorAll("[data-k]").forEach(
      (input) =>
        (input.onchange = () => {
          const k = input.dataset.k;
          draftStates[i][k] =
            input.type === "checkbox"
              ? input.checked
              : input.type === "number"
                ? input.value === ""
                  ? null
                  : Number(input.value)
                : input.value;
          if (k === "type") renderStateRows(draftStates);
        }),
    );
    row.querySelector("button").onclick = () => {
      draftStates.splice(i, 1);
      renderStateRows(draftStates);
    };
    root.append(row);
  });
}
function applyProjectSettings() {
  snapshot();
  const oldIds = new Map(
    draftStates
      .filter((s) => s.__originalId)
      .map((s) => [s.__originalId, s.id]),
  );
  state.project.pack = {
    ...(state.project.pack || {}),
    id: $("#setting-pack-id").value,
    namespace: $("#setting-namespace").value,
    title: $("#setting-title").value,
    format: Number($("#setting-format").value),
  };
  state.project.entry = $("#setting-entry").value;
  state.project.allowRawMechanics = $("#setting-raw").checked;
  state.project.state = clone(draftStates).map(({ __originalId, ...s }) => s);
  const reference = (id) => {
    id = oldIds.get(id) || id;
    const declared = state.project.state.find((s) => s.id === id);
    return {
      id,
      type: declared?.type || "COOKIE",
      scope: declared?.scope || "PLAYER",
    };
  };
  state.project.contract.requires = csv($("#setting-requires").value).map(
    reference,
  );
  state.project.contract.provides = csv($("#setting-provides").value).map(
    reference,
  );
  state.project.contract.outcomes = $("#setting-outcomes")
    .value.split(/\r?\n/)
    .map((x) => x.trim())
    .filter(Boolean);
  for (const n of state.project.nodes) {
    for (const c of n.conditions)
      if (oldIds.get(c.state)) c.state = oldIds.get(c.state);
    for (const a of n.actions)
      if (oldIds.get(a.state)) a.state = oldIds.get(a.state);
    for (const r of n.responses)
      for (const a of r.actions)
        if (oldIds.get(a.state)) a.state = oldIds.get(a.state);
  }
  $("#project-dialog").close();
  changed();
}

function openGenerationStudio() {
  const brief = state.provenance?.brief;
  $("#gen-id").value = brief?.id || `${state.project.pack.id}_generated`;
  $("#gen-title").value =
    brief?.title || `${state.project.pack.title} Generated`;
  $("#gen-prompt").value = brief?.prompt || "";
  $("#gen-min").value = brief?.constraints?.minimumCards || 6;
  $("#gen-max").value = brief?.constraints?.maximumCards || 12;
  $("#gen-dialogue-limit").value =
    brief?.constraints?.maximumDialogueCharacters || 160;
  $("#gen-dialogue-style").value =
    brief?.constraints?.dialogueStyle ||
    "Concise, playful internet-anime banter. Be expressive and lightly meme-y; use occasional rawr/xd energy only when it suits the character, never as constant noise.";
  $("#gen-characters").value = (brief?.characters || []).join(", ");
  $("#gen-subject").value = brief?.subjects?.[0]?.id || "";
  $("#gen-auto-context").checked = brief?.automaticContext !== false;
  $("#gen-documents").value = (brief?.documents || []).join("\n");
  $("#gen-provider").value = brief?.provider || "openai";
  updateGenerationProviderFields();
  $("#gen-model").value = brief?.model || "gpt-5.6-luna";
  $("#gen-recorded").value = workingPath(
    brief?.recordedResponses ||
      "tools/conversation-core/examples/generation/responses/flower",
  );
  $("#gen-calls").value = brief?.budget?.maximumCalls || 12;
  const base = $("#export-path").value.replace(/[\\/]?[^\\/]+$/, "");
  $("#gen-output").value = `${base}\\${$("#gen-id").value}`;
  $("#gen-solution-mode").value = state.session?.solutionOpen ? "active" : "none";
  $("#gen-solution-path").value = state.session?.solutionPath || "";
  $("#gen-solution-name").value = "";
  $("#gen-solution-group").value = "Projects";
  updateGenerationSolutionFields();
  $("#catalog-preview").innerHTML = "";
  renderGenerationStatus({ state: "IDLE", stage: "IDLE", calls: 0 });
  $("#generation-dialog").showModal();
}
function updateGenerationSolutionFields() {
  const mode = $("#gen-solution-mode").value;
  if (mode === "active" && !state.session?.solutionOpen) {
    $("#gen-solution-mode").value = "none";
    return updateGenerationSolutionFields();
  }
  $("#gen-solution-path-field").hidden = mode !== "existing" && mode !== "create";
  $("#gen-solution-name-field").hidden = mode !== "create";
  $("#gen-solution-group-field").hidden = mode === "none";
  $("#gen-solution-hint").textContent = mode === "none"
    ? "The generated project will remain standalone."
    : mode === "active"
      ? `The generated project will be added to ${state.session.solution.name}.`
      : mode === "create"
        ? "A new solution will be created and opened when generation succeeds."
        : "The solution will be opened and updated when generation succeeds.";
}

function generationSolutionTarget() {
  const mode = $("#gen-solution-mode").value;
  if (mode === "none") return null;
  const solution = mode === "active" ? state.session.solutionPath : workingPath($("#gen-solution-path").value.trim());
  const name = mode === "create" ? $("#gen-solution-name").value.trim() : null;
  if (!solution) throw new Error("Choose a solution file.");
  if (mode === "create" && !name) throw new Error("Name the new solution.");
  return { solution, name, group: $("#gen-solution-group").value.trim() || "Projects" };
}
function updateGenerationProviderFields() {
  $("#gen-recorded-field").hidden = $("#gen-provider").value !== "recorded";
}
function generationBrief() {
  const provider = $("#gen-provider").value;
  const brief = {
    generationFormat: 1,
    id: $("#gen-id").value,
    namespace: state.project.pack.namespace || "block_party_generated",
    title: $("#gen-title").value,
    prompt: $("#gen-prompt").value,
    subjects: $("#gen-subject").value.trim()
      ? [
          {
            kind: "BLOCK",
            id: $("#gen-subject").value.trim(),
            role: "PRIMARY",
          },
        ]
      : [],
    automaticContext: $("#gen-auto-context").checked,
    characters: csv($("#gen-characters").value),
    documents: $("#gen-documents")
      .value.split(/\r?\n/)
      .map((x) => x.trim())
      .filter(Boolean),
    existingPacks: [],
    constraints: {
      minimumCards: Number($("#gen-min").value),
      maximumCards: Number($("#gen-max").value),
      maximumDialogueCharacters: Number($("#gen-dialogue-limit").value),
      dialogueStyle: $("#gen-dialogue-style").value.trim(),
      requiredFeatures: [],
      allowedActions: state.schema.actionTypes.filter((x) => x !== "RAW"),
      allowedConditions: state.schema.conditionTypes.filter((x) => x !== "RAW"),
      responseCues: state.schema.cues,
    },
    budget: {
      maximumCalls: Number($("#gen-calls").value),
      maximumInputCharacters: 500000,
      maximumOutputCharacters: 200000,
    },
    provider,
    model: $("#gen-model").value,
  };
  if (provider === "recorded") brief.recordedResponses = $("#gen-recorded").value;
  return brief;
}
$("#gen-provider").onchange = updateGenerationProviderFields;
async function previewCatalog() {
  try {
    const catalog = await api("catalog", { brief: generationBrief() });
    const context = catalog.context || { inclusions: [], warnings: [] };
    const reasonNames = {
      MANDATORY_WORLD_RULE: "World rules",
      TAG: "Resolved block traits",
      BLOCK_PROFILE: "Block context",
      CHARACTER_PROFILE: "Character context",
    };
    const groups = (context.inclusions || []).reduce((result, item) => {
      (result[item.reason] ??= []).push(item);
      return result;
    }, {});
    const automatic = Object.entries(groups)
      .map(
        ([reason, items]) =>
          `<section class="context-group"><h4>${esc(reasonNames[reason] || reason)}</h4>${items
            .map(
              (item) =>
                `<div class="catalog-document"><b>${esc(item.title)}</b>${item.source ? `<small>${esc(item.source)}</small>` : ""}${item.content.length} characters · ${esc(item.sha256.slice(0, 12))}…</div>`,
            )
            .join("")}</section>`,
      )
      .join("");
    const automaticPaths = new Set(
      (context.inclusions || []).map((item) => `context/${item.path}`),
    );
    const creatorDocuments = catalog.documents.filter(
      (document) => !automaticPaths.has(document.path),
    );
    $("#catalog-preview").innerHTML =
      `<p class="all-clear">${catalog.documents.length} bounded document(s); ${catalog.actions.length} action and ${catalog.conditions.length} condition types.</p>` +
      (context.warnings || [])
        .map((warning) => `<p class="diagnostic WARNING">${esc(warning)}</p>`)
        .join("") +
      automatic +
      (creatorDocuments.length
        ? '<section class="context-group"><h4>Creator documents</h4>'
        : "") +
      creatorDocuments
        .map(
          (d) =>
            `<div class="catalog-document"><b>${esc(d.path)}</b>${d.content.length} characters · ${esc(d.sha256.slice(0, 12))}…</div>`,
        )
        .join("") +
      (creatorDocuments.length ? "</section>" : "");
  } catch (e) {
    $("#catalog-preview").innerHTML =
      `<p class="diagnostic ERROR">${esc(e.message)}</p>`;
  }
}
async function startGeneration() {
  try {
    state.pendingSolutionTarget = generationSolutionTarget();
    await api("generation/start", {
      brief: generationBrief(),
      output: $("#gen-output").value,
    });
    pollGeneration();
  } catch (e) {
    renderGenerationStatus({
      state: "FAILED",
      stage: "FAILED",
      calls: 0,
      error: e.message,
    });
  }
}
const generationStages = [
  "CATALOG",
  "ARC_PLAN",
  "GRAPH",
  "GRAPH_REPAIR",
  "INTENTIONS",
  "DIALOGUE",
  "REVIEW",
  "COMPLETE",
];
function renderGenerationStatus(status) {
  const active = status.stage.replace("_COMPLETE", "");
  const activeIndex = generationStages.indexOf(active);
  $("#generation-progress").innerHTML =
    `<div class="stage-list">${generationStages.map((name, i) => `<div class="stage ${status.state === "FAILED" && name === active ? "failed" : i < activeIndex || status.stage === name + "_COMPLETE" || status.state === "COMPLETE" ? "complete" : name === active ? "active" : ""}"><span>${name.replaceAll("_", " ")}</span><b>${i < activeIndex || status.state === "COMPLETE" ? "✓" : name === active && status.state === "RUNNING" ? "working" : ""}</b></div>`).join("")}</div>${status.error ? `<p class="diagnostic ERROR">${esc(status.error)}</p>` : ""}`;
}
async function pollGeneration() {
  const status = await api("generation/status");
  renderGenerationStatus(status);
  if (status.state === "RUNNING") setTimeout(pollGeneration, 500);
  else if (status.state === "COMPLETE") {
    if (state.pendingSolutionTarget) {
      const target = state.pendingSolutionTarget;
      state.session = await api("solution/project/register", {
        ...target,
        project: $("#gen-output").value,
      });
      state.pendingSolutionTarget = null;
    }
    toast("Generation complete");
    localStorage.removeItem(recoveryKey());
    await loadProject();
  }
}

function renderProvenance() {
  const p = state.provenance;
  const root = $("#provenance-content");
  if (!p?.available) {
    root.innerHTML =
      '<p class="hint">This project has no generation archive. Generate a pack or open an Iteration 3 output directory.</p>';
    return;
  }
  const manifest = p.manifest || {};
  $("#review-context").value = p["review-context"]?.notes || "";
  const findings = p.review?.findings || [];
  const findingList = findings.length
    ? `<h3>Review findings</h3><div class="review-findings">${findings.map((finding) => `<div class="diagnostic ${esc((finding.severity || "warning").toUpperCase())}"${finding.node ? ` data-review-node="${attr(finding.node)}"` : ""}><b>${esc((finding.severity || "warning").toUpperCase())} · ${esc(finding.code || "review")}${finding.node ? ` · ${esc(finding.node)}` : ""}</b><p>${esc(finding.message || "")}</p></div>`).join("")}</div>`
    : '<h3>Review findings</h3><p class="hint">No editorial findings.</p>';
  root.innerHTML = `<div class="provenance-summary"><div class="provenance-panel"><h3>Brief</h3><b>${esc(p.brief?.title || "")}</b><p>${esc(p.brief?.prompt || "")}</p></div><div class="provenance-panel"><h3>Context</h3><b>${p.context?.inclusions?.length || 0} automatic include(s)</b><p>${p.context?.warnings?.length || 0} warning(s)</p></div><div class="provenance-panel"><h3>Usage</h3><b>${manifest.calls || 0} calls</b><p>${manifest.input_tokens || 0} input · ${manifest.output_tokens || 0} output tokens</p></div><div class="provenance-panel"><h3>Intentions</h3><b>${p.intentions?.scenes?.length || 0} dialogue cards</b></div><div class="provenance-panel"><h3>Review</h3><b>${findings.length} finding(s)</b></div></div>${findingList}<h3>Archived stages</h3>${(p.stages || []).map((s) => `<details class="stage-archive"><summary><b>${esc(s.metadata?.stage || s.directory)}</b><span>${esc(s.metadata?.provider || "")} · ${esc(s.metadata?.model || "")}</span></summary><h4>Request</h4><pre class="archive-json">${esc(JSON.stringify(s.request, null, 2))}</pre><h4>Response</h4><pre class="archive-json">${esc(JSON.stringify(s.response, null, 2))}</pre></details>`).join("")}`;
  root.querySelectorAll("[data-review-node]").forEach((finding) => {
    finding.onclick = () => {
      $("#review-dialog").close();
      select(finding.dataset.reviewNode, true);
    };
  });
}

async function rerunReview() {
  if (state.diagnostics.some((diagnostic) => diagnostic.severity === "ERROR")) {
    return toast("Resolve validation errors before re-running review.", true);
  }
  const button = $("#rerun-review");
  button.disabled = true;
  button.textContent = "Reviewing…";
  try {
    const result = await api("review/rerun", {
      project: state.project,
      authorContext: $("#review-context").value.trim(),
    });
    state.provenance = result.provenance;
    state.dirty = false;
    localStorage.removeItem(recoveryKey());
    $("#dirty").textContent = "Saved";
    $("#save").disabled = true;
    renderProvenance();
    renderDiagnostics();
    renderCanvas();
    toast(result.publishable ? "Review passed; export is unlocked." : "Review still has blocking findings.", !result.publishable);
  } catch (error) {
    toast(error.message, true);
  } finally {
    button.disabled = false;
    button.textContent = "Re-run review";
  }
}

function openRevision() {
  if (!selected()) return;
  if (!$("#revision-recorded").value.match(/^(?:[a-zA-Z]:[\\/]|\/)/)) {
    $("#revision-recorded").value = workingPath($("#revision-recorded").value);
  }
  $("#revision-alternatives").innerHTML = "";
  $("#revision-dialog").showModal();
}
async function requestRevision() {
  const button = $("#request-revision");
  button.disabled = true;
  try {
    const revision = await api("revision/request", {
      project: state.project,
      node: state.selected,
      instruction: $("#revision-instruction").value,
      provider: $("#revision-provider").value,
      model: $("#revision-model").value,
      recordedResponses: $("#revision-recorded").value,
    });
    $("#revision-alternatives").innerHTML = revision.alternatives
      .map(
        (a, i) =>
          `<article class="alternative"><small>ALTERNATIVE ${i + 1}</small><blockquote>${esc(a.text)}</blockquote><p class="alternative-labels">Responses: ${(a.responseLabels || []).map(esc).join(" · ")}</p><p class="hint">${esc(a.rationale || "")}</p><div class="alternative-actions"><button type="button" data-alt="${i}" class="primary">Accept alternative</button></div></article>`,
      )
      .join("");
    $$("[data-alt]").forEach(
      (b) =>
        (b.onclick = () =>
          applyRevision(revision.alternatives[Number(b.dataset.alt)])),
    );
  } catch (e) {
    $("#revision-alternatives").innerHTML =
      `<p class="diagnostic ERROR">${esc(e.message)}</p>`;
  } finally {
    button.disabled = false;
  }
}
async function applyRevision(alternative) {
  try {
    const revised = await api("revision/apply", {
      project: state.project,
      node: state.selected,
      alternative,
    });
    snapshot();
    state.project = revised;
    $("#revision-dialog").close();
    changed();
    toast("Prose revision accepted; mechanics unchanged");
  } catch (e) {
    toast(e.message, true);
  }
}

let validationTimer;
function scheduleValidate() {
  clearTimeout(validationTimer);
  validationTimer = setTimeout(validate, 280);
}
async function validate() {
  try {
    const report = await api("validate", { project: state.project });
    state.diagnostics = report.diagnostics || [];
    renderDiagnostics();
    renderCanvas();
  } catch (e) {
    toast(e.message, true);
  }
}
function renderDiagnostics() {
  const root = $("#diagnostics");
  root.innerHTML = "";
  const reviewBlockers = blockingReviewFindings();
  $("#check-count").textContent = state.diagnostics.length + reviewBlockers.length || "✓";
  if (!state.diagnostics.length && !reviewBlockers.length) {
    root.innerHTML =
      '<div class="all-clear">Everything connects cleanly.</div>';
    return;
  }
  for (const d of state.diagnostics) {
    const item = document.createElement("div");
    item.className = "diagnostic " + d.severity;
    item.innerHTML = `<b>${esc(d.severity)} · ${esc(d.code)}</b>${esc(d.message)}`;
    if (d.node) item.onclick = () => select(d.node, true);
    root.append(item);
  }
  for (const finding of reviewBlockers) {
    const item = document.createElement("div");
    item.className = "diagnostic HIGH";
    item.innerHTML = `<b>HIGH REVIEW · ${esc(finding.code || "review")}</b>${esc(finding.message || "")}`;
    if (finding.node) item.onclick = () => select(finding.node, true);
    root.append(item);
  }
}

async function save() {
  if (state.diagnostics.some((d) => d.severity === "ERROR"))
    return toast("Resolve validation errors before saving.", true);
  try {
    await api("save", { project: state.project });
    state.dirty = false;
    localStorage.removeItem(recoveryKey());
    $("#dirty").textContent = "Saved";
    $("#save").disabled = true;
    toast("Project saved");
  } catch (e) {
    toast(e.message, true);
  }
}
async function runSimulation() {
  try {
    const scenario = {
      cookies: json($("#scenario-cookies").value),
      counters: json($("#scenario-counters").value),
      inventory: json($("#scenario-inventory").value),
    };
    const r = await api("simulate", { project: state.project, scenario });
    state.traces = r.traces || [];
    state.trace = 0;
    state.step = 0;
    $("#simulation-results").innerHTML =
      `<div class="stat-grid"><div class="stat"><b>${r.routes}</b><span>Routes</span></div><div class="stat"><b>${r.endings.length}</b><span>Endings</span></div><div class="stat"><b>${r.cycles.length}</b><span>Cycles</span></div></div><label>Debug route<select id="trace-select">${state.traces.map((t, i) => `<option value="${i}">Route ${i + 1} · ${esc(t[t.length - 1] || "")}</option>`).join("")}</select></label><p><b>External requirements:</b> ${r.externalRequirements.map(esc).join(", ") || "none"}</p>`;
    $("#trace-select")?.addEventListener("change", (e) => {
      state.trace = Number(e.target.value);
      state.step = 0;
      renderTrace();
    });
    renderTrace();
  } catch (e) {
    $("#simulation-results").innerHTML =
      `<p class="diagnostic ERROR">${esc(e.message)}</p>`;
  }
}
function renderTrace() {
  const trace = state.traces[state.trace];
  $("#trace-debugger").hidden = !trace;
  if (!trace) return;
  state.step = Math.max(0, Math.min(state.step, trace.length - 1));
  $("#trace-position").textContent = `${state.step + 1} / ${trace.length}`;
  $("#trace-step").textContent = trace[state.step];
  $("#trace-prev").disabled = state.step === 0;
  $("#trace-next").disabled = state.step === trace.length - 1;
}
function saveScenario() {
  localStorage.setItem(
    "block-party-scenario",
    JSON.stringify({
      cookies: $("#scenario-cookies").value,
      counters: $("#scenario-counters").value,
      inventory: $("#scenario-inventory").value,
    }),
  );
  toast("Scenario remembered on this device");
}
function restoreScenario() {
  try {
    const s = JSON.parse(localStorage.getItem("block-party-scenario"));
    if (s) {
      $("#scenario-cookies").value = s.cookies;
      $("#scenario-counters").value = s.counters;
      $("#scenario-inventory").value = s.inventory;
    }
  } catch {}
}
async function runExport() {
  try {
    const liveResources = $("#export-live-resources").checked;
    const r = await api("export", {
      project: state.project,
      output: $("#export-path").value,
      liveResources,
    });
    $("#export-result").innerHTML =
      `<p class="all-clear">${liveResources ? "Updated" : "Exported"} ${r.datapackFiles} scene pack files across ${r.routes} routes to<br><b>${esc(r.output)}</b>${liveResources ? "<br>Run <code>/reload</code> in the development world to reload datapacks." : ""}</p>`;
  } catch (e) {
    $("#export-result").innerHTML =
      `<p class="diagnostic ERROR">${esc(e.message)}</p>`;
  }
}

$("#card-form").addEventListener("change", (e) => {
  if (!e.target.name) return;
  const key = e.target.name;
  editSelected((n) => {
    if (key === "id") {
      const old = n.id;
      n.id = e.target.value;
      if (state.project.entry === old) state.project.entry = n.id;
      for (const x of state.project.nodes) {
        if (x.next === old) x.next = n.id;
        for (const r of x.responses) if (r.target === old) r.target = n.id;
      }
      state.selected = n.id;
    } else if (key.startsWith("selection")) {
      n.selection ??= { group: "", weight: 1, cooldownTicks: 0 };
      if (key === "selectionGroup") n.selection.group = e.target.value;
      if (key === "selectionWeight") n.selection.weight = Number(e.target.value) || 1;
      if (key === "selectionCooldownTicks") n.selection.cooldownTicks = Number(e.target.value) || 0;
    } else if (key === "emotion" || key === "animation") {
      n.speaker ??= {};
      if (e.target.value) n.speaker[key] = e.target.value;
      else delete n.speaker[key];
      if (!n.speaker.emotion && !n.speaker.animation) n.speaker = null;
    } else n[key] = e.target.value || null;
  });
});
$("#add-condition").onclick = () =>
  editSelected((n) => n.conditions.push({ type: "ALWAYS" }));
$("#add-action").onclick = () =>
  editSelected((n) =>
    n.actions.push({
      type: "SET_COOKIE",
      scope: "PLAYER",
      state: "",
      value: "true",
    }),
  );
$("#add-response").onclick = () => {
  const n = selected();
  if (n.responses.length >= state.schema.maximumResponses) {
    toast(`Dialogue cards support at most ${state.schema.maximumResponses} responses.`, true);
    return;
  }
  editSelected((n) =>
    n.responses.push({
      cue: "chat_bubble",
      label: "Continue",
      target: "",
      transition: "IMMEDIATE",
      actions: [],
    }),
  );
};
$("#add-card").onclick = () => {
  snapshot();
  let i = 1;
  while (state.project.nodes.some((n) => n.id === `new_card_${i}`)) i++;
  const n = {
    id: `new_card_${i}`,
    type: "DIALOGUE",
    title: "New card",
    trigger: null,
    conditions: [],
    text: "",
    tooltip: false,
    speaker: null,
    responses: [],
    actions: [],
    next: null,
    ending: null,
    editor: { x: 120, y: 120 },
  };
  state.project.nodes.push(n);
  state.selected = n.id;
  changed();
};
$("#duplicate-card").onclick = () => {
  const source = selected();
  if (!source) return;
  snapshot();
  let i = 1;
  while (state.project.nodes.some((n) => n.id === `${source.id}_copy_${i}`))
    i++;
  const copy = clone(source);
  copy.id = `${source.id}_copy_${i}`;
  copy.title = `${source.title || source.id} copy`;
  copy.editor = { x: source.editor.x + 40, y: source.editor.y + 40 };
  state.project.nodes.push(copy);
  state.selected = copy.id;
  changed();
};
$("#delete-card").onclick = () => {
  const n = selected();
  if (!n || !confirm(`Delete ${n.id}? Incoming links will need repair.`))
    return;
  snapshot();
  state.project.nodes = state.project.nodes.filter((x) => x !== n);
  state.selected = null;
  changed();
};
$("#undo").onclick = () => restore(state.undo, state.redo);
$("#redo").onclick = () => restore(state.redo, state.undo);
$("#save").onclick = save;
$("#session-home").onclick = returnToStart;
$("#connect-card").onclick = startConnection;
$("#arrange").onclick = arrange;
$("#project-settings").onclick = openProjectSettings;
$("#add-state").onclick = () => {
  draftStates.push({
    id: "new_state",
    type: "COOKIE",
    scope: "PLAYER",
    initialCookie: false,
    initialCounter: 0,
    minimum: null,
    maximum: null,
  });
  renderStateRows(draftStates);
};
$("#apply-project").onclick = applyProjectSettings;
$("#generation-studio").onclick = openGenerationStudio;
$("#preview-catalog").onclick = previewCatalog;
$("#start-generation").onclick = startGeneration;
$("#review-generation").onclick = () => {
  renderProvenance();
  $("#review-dialog").showModal();
};
$("#rerun-review").onclick = rerunReview;
$("#review-first-blocker").onclick = () => {
  const finding = blockingReviewFindings().find((candidate) => candidate.node);
  if (!finding) return toast("No card-specific blocking finding.");
  $("#review-dialog").close();
  select(finding.node, true);
};
$("#revise-card").onclick = openRevision;
$("#request-revision").onclick = requestRevision;
$("#simulate").onclick = () => $("#simulation-dialog").showModal();
$("#reveal-cards").onclick = () => {
  const shifted = normalizeEditorPositions(state.project.nodes);
  if (shifted) changed();
  else renderCanvas();
  $("#viewport").scrollTo({ left: 0, top: 0, behavior: "smooth" });
};
$("#run-simulation").onclick = runSimulation;
$("#save-scenario").onclick = saveScenario;
$("#trace-prev").onclick = () => {
  state.step--;
  renderTrace();
};
$("#trace-next").onclick = () => {
  state.step++;
  renderTrace();
};
const exportButton = document.createElement("button");
exportButton.textContent = "Export";
exportButton.onclick = () => {
  $("#export-live-resources").checked = false;
  $("#export-path").disabled = false;
  $("#export-path").value = $("#export-path").dataset.standard || "";
  $("#export-result").innerHTML = "";
  $("#export-dialog").showModal();
};
$("#simulate").after(exportButton);
$("#run-export").onclick = runExport;
$("#export-live-resources").onchange = (event) => {
  const live = event.target.checked;
  $("#export-path").value = live
    ? $("#export-path").dataset.live || ""
    : $("#export-path").dataset.standard || "";
  $("#export-path").disabled = live;
  $("#export-result").innerHTML = "";
};
$("#search").oninput = (e) => {
  state.search = e.target.value;
  renderOutline();
  renderCanvas();
};
$$(".filter").forEach(
  (b) =>
    (b.onclick = () => {
      $$(".filter").forEach((x) => x.classList.remove("active"));
      b.classList.add("active");
      state.filter = b.dataset.filter;
      renderOutline();
      renderCanvas();
    }),
);
addEventListener("keydown", (e) => {
  if (e.key === "Escape" && state.connecting) {
    state.connecting = null;
    $("#connect-card").classList.remove("active");
    renderCanvas();
  }
  if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === "s") {
    e.preventDefault();
    save();
  }
  if ((e.ctrlKey || e.metaKey) && e.key === "Tab" && state.session?.documents?.length > 1) {
    e.preventDefault();
    const documents = state.session.documents;
    const current = documents.findIndex(item => item.id === state.session.activeDocument);
    const next = (current + (e.shiftKey ? -1 : 1) + documents.length) % documents.length;
    if (!state.dirty || confirm("Switch projects with unsaved changes?")) {
      api("document/activate", { document: documents[next].id }).then(session => { state.session = session; return loadProject(); }).catch(error => toast(error.message, true));
    }
  }
  if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === "z") {
    e.preventDefault();
    restore(
      e.shiftKey ? state.redo : state.undo,
      e.shiftKey ? state.undo : state.redo,
    );
  }
});
addEventListener("beforeunload", (e) => {
  if (state.dirty) {
    e.preventDefault();
    e.returnValue = "";
  }
});
function toast(message, error = false) {
  const t = $("#toast");
  t.textContent = message;
  t.style.background = error ? "#9d3831" : "";
  t.classList.add("show");
  setTimeout(() => t.classList.remove("show"), 2400);
}
function json(s) {
  return JSON.parse(s || "{}");
}
function csv(s) {
  return s
    .split(",")
    .map((x) => x.trim())
    .filter(Boolean);
}
function words(s) {
  return s.replace(/([A-Z])/g, " $1").replace(/^./, (c) => c.toUpperCase());
}
function esc(s) {
  return String(s ?? "").replace(
    /[&<>"']/g,
    (c) =>
      ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[
        c
      ],
  );
}
function attr(s) {
  return esc(s);
}
$$("[data-start-mode]").forEach(
  (button) => (button.onclick = () => openStartPanel(button.dataset.startMode)),
);
$$(".start-cancel").forEach((button) => (button.onclick = showStartScreen));
$("#start-title").oninput = () => {
  if (!$("#start-id").dataset.edited) {
    $("#start-id").value = slug($("#start-title").value);
  }
};
$("#start-id").oninput = () => {
  $("#start-id").dataset.edited = "true";
};
$("#start-create").onclick = createPack;
$("#start-open").onclick = () => openPack($("#start-open-path").value.trim());
$("#start-open-solution").onclick = () => openSolution($("#start-solution-path").value.trim());
$("#start-create-solution").onclick = createSolution;
$("#gen-solution-mode").onchange = updateGenerationSolutionFields;
$("#close-compare").onclick = () => { state.compareDocument = null; $("#compare-pane").hidden = true; };
$("#add-solution-project").onclick = async () => {
  const path = prompt("Project file or generation directory to add:");
  if (!path) return;
  const group = prompt("Solution group:", "Projects") || "Projects";
  try { state.session = await api("solution/project/add", { path: workingPath(path), group }); renderWorkspaceChrome(); }
  catch (error) { toast(error.message, true); }
};
bootstrap();
