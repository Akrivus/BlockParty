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
};
const recentKey = "block-party-workbench:recent-packs";
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
      api("project"),
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
  $("#start-working-directory").textContent = state.session?.workingDirectory
    ? `Working directory: ${state.session.workingDirectory}`
    : "";
  renderRecents();
}

function recentPacks() {
  try {
    return JSON.parse(localStorage.getItem(recentKey) || "[]");
  } catch {
    return [];
  }
}

function rememberRecent(path, title) {
  const recents = recentPacks().filter((item) => item.path !== path);
  recents.unshift({ path, title: title || path, opened: Date.now() });
  localStorage.setItem(recentKey, JSON.stringify(recents.slice(0, 8)));
}

function renderRecents() {
  const root = $("#recent-packs");
  const recents = recentPacks();
  if (!recents.length) {
    root.innerHTML =
      '<div class="recent-empty">Packs you open will appear here.</div>';
    return;
  }
  root.innerHTML = recents
    .map(
      (item, index) =>
        `<div class="recent-pack"><button type="button" data-recent-open="${index}"><strong>${esc(item.title)}</strong><small>${esc(item.path)}</small></button><button type="button" data-recent-remove="${index}" title="Remove from recent packs">×</button></div>`,
    )
    .join("");
  $$("[data-recent-open]").forEach(
    (button) =>
      (button.onclick = () =>
        openPack(recents[Number(button.dataset.recentOpen)].path)),
  );
  $$("[data-recent-remove]").forEach(
    (button) =>
      (button.onclick = () => {
        recents.splice(Number(button.dataset.recentRemove), 1);
        localStorage.setItem(recentKey, JSON.stringify(recents));
        renderRecents();
      }),
  );
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
  $("#start-create-panel").hidden = mode === "open";
  $("#start-open-panel").hidden = mode !== "open";
  if (mode !== "open") {
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
  } else {
    $("#start-open-path").focus();
  }
}

async function openPack(path) {
  try {
    state.session = await api("open", { path });
    await loadProject();
    rememberRecent(state.path, state.project.pack?.title);
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
    });
    await loadProject();
    rememberRecent(state.path, title);
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
  const shown = new Set(state.project.nodes.filter(visible).map((n) => n.id));
  for (const n of state.project.nodes.filter(visible)) {
    const card = document.createElement("article");
    card.className = `node-card ${n.type}${n.id === state.selected ? " selected" : ""}${state.diagnostics.some((d) => d.node === n.id && d.severity === "ERROR") ? " has-error" : ""}${state.connecting === n.id ? " connect-source" : state.connecting ? " connect-target" : ""}`;
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
        }),
      );
    root.append(box);
  });
}
function primitiveField(name, value, kind, commit) {
  const label = document.createElement("label");
  label.textContent = words(name);
  let input;
  const enumValues = state.schema.enums[name];
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
    ["count", "amount", "ticks", "minGameDays"].includes(name) ||
    (name === "value" && kind === "condition")
  ) {
    input = document.createElement("input");
    input.type = "number";
    input.value = value ?? 0;
  } else if (name === "raw") {
    input = document.createElement("textarea");
    input.value = JSON.stringify(value ?? {}, null, 2);
  } else {
    input = document.createElement("input");
    input.value = value ?? "";
    if (name === "state") input.setAttribute("list", "state-ids");
  }
  input.onchange = () => {
    if (input.type === "checkbox") commit(input.checked);
    else if (input.type === "number") commit(Number(input.value));
    else if (name === "raw") {
      try {
        commit(JSON.parse(input.value));
      } catch (e) {
        toast("Raw value must be valid JSON.", true);
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
  $("#gen-model").value = brief?.model || "gpt-5.6-luna";
  $("#gen-recorded").value = workingPath(
    brief?.recordedResponses ||
      "tools/conversation-core/examples/generation/responses/flower",
  );
  $("#gen-calls").value = brief?.budget?.maximumCalls || 12;
  const base = $("#export-path").value.replace(/[\\/]?[^\\/]+$/, "");
  $("#gen-output").value = `${base}\\${$("#gen-id").value}`;
  $("#catalog-preview").innerHTML = "";
  renderGenerationStatus({ state: "IDLE", stage: "IDLE", calls: 0 });
  $("#generation-dialog").showModal();
}
function generationBrief() {
  return {
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
    provider: $("#gen-provider").value,
    model: $("#gen-model").value,
    recordedResponses: $("#gen-recorded").value,
  };
}
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
    toast("Generation complete");
    localStorage.removeItem(recoveryKey());
    await loadProject();
    rememberRecent(state.path, state.project.pack?.title);
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
  const findings = p.review?.findings || [];
  const findingList = findings.length
    ? `<h3>Review findings</h3><div class="review-findings">${findings.map((finding) => `<div class="diagnostic ${esc((finding.severity || "warning").toUpperCase())}"><b>${esc((finding.severity || "warning").toUpperCase())} · ${esc(finding.code || "review")}${finding.node ? ` · ${esc(finding.node)}` : ""}</b><p>${esc(finding.message || "")}</p></div>`).join("")}</div>`
    : '<h3>Review findings</h3><p class="hint">No editorial findings.</p>';
  root.innerHTML = `<div class="provenance-summary"><div class="provenance-panel"><h3>Brief</h3><b>${esc(p.brief?.title || "")}</b><p>${esc(p.brief?.prompt || "")}</p></div><div class="provenance-panel"><h3>Context</h3><b>${p.context?.inclusions?.length || 0} automatic include(s)</b><p>${p.context?.warnings?.length || 0} warning(s)</p></div><div class="provenance-panel"><h3>Usage</h3><b>${manifest.calls || 0} calls</b><p>${manifest.input_tokens || 0} input · ${manifest.output_tokens || 0} output tokens</p></div><div class="provenance-panel"><h3>Intentions</h3><b>${p.intentions?.scenes?.length || 0} dialogue cards</b></div><div class="provenance-panel"><h3>Review</h3><b>${findings.length} finding(s)</b></div></div>${findingList}<h3>Archived stages</h3>${(p.stages || []).map((s) => `<details class="stage-archive"><summary><b>${esc(s.metadata?.stage || s.directory)}</b><span>${esc(s.metadata?.provider || "")} · ${esc(s.metadata?.model || "")}</span></summary><h4>Request</h4><pre class="archive-json">${esc(JSON.stringify(s.request, null, 2))}</pre><h4>Response</h4><pre class="archive-json">${esc(JSON.stringify(s.response, null, 2))}</pre></details>`).join("")}`;
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
  $("#check-count").textContent = state.diagnostics.length || "✓";
  if (!state.diagnostics.length) {
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
$("#add-response").onclick = () =>
  editSelected((n) =>
    n.responses.push({
      cue: "chat_bubble",
      label: "Continue",
      target: "",
      transition: "IMMEDIATE",
      actions: [],
    }),
  );
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
$("#revise-card").onclick = openRevision;
$("#request-revision").onclick = requestRevision;
$("#simulate").onclick = () => $("#simulation-dialog").showModal();
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
bootstrap();
