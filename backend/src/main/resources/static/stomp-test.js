(() => {
  const $ = (id) => document.getElementById(id);
  const controls = ["disconnect", "subscribe-chat", "subscribe-events", "subscribe-errors", "send-chat"];
  let client;
  let subscriptions = new Map();

  function log(message, type = "info", payload) {
    const entry = document.createElement("div");
    entry.className = `log-entry ${type}`;
    const timestamp = new Date().toLocaleTimeString();
    entry.textContent = `[${timestamp}] ${message}${payload === undefined ? "" : `\n${formatPayload(payload)}`}`;
    $("log").append(entry);
    $("log").scrollTop = $("log").scrollHeight;
    if (type === "error") console.error(message, payload);
    else console.log(message, payload ?? "");
  }

  function formatPayload(payload) {
    try { return JSON.stringify(typeof payload === "string" ? JSON.parse(payload) : payload, null, 2); }
    catch { return String(payload); }
  }

  function setConnected(connected, error = false) {
    const badge = $("status");
    badge.textContent = error ? "ERROR" : connected ? "CONNECTED" : "DISCONNECTED";
    badge.className = `status${connected ? " connected" : ""}${error ? " error" : ""}`;
    $("connect").disabled = connected;
    controls.forEach((id) => { $(id).disabled = !connected; });
  }

  function destination(suffix) { return `/topic/trials/${$("trial-id").value.trim()}/${suffix}`; }

  function subscribe(key, target, type) {
    if (subscriptions.has(key)) {
      log(`Already subscribed: ${target}`);
      return;
    }
    subscriptions.set(key, client.subscribe(target, (message) => log(`RECEIVED ${target}`, type, message.body)));
    log(`SUBSCRIBED ${target}`, "success");
  }

  $("connect").addEventListener("click", () => {
    const brokerURL = $("broker-url").value.trim();
    const userId = $("demo-user-id").value.trim();
    client = new StompJs.Client({
      brokerURL,
      connectHeaders: { "X-Demo-User-Id": userId },
      reconnectDelay: 0,
      debug: (line) => console.debug("[STOMP]", line),
      onConnect: () => { setConnected(true); log(`CONNECTED ${brokerURL} as ${userId || "(missing header)"}`, "success"); },
      onStompError: (frame) => { setConnected(false, true); log("STOMP ERROR frame", "error", frame.headers["message"] || frame.body); },
      onWebSocketError: (event) => { setConnected(false, true); log("WebSocket error", "error", event); },
      onWebSocketClose: (event) => { subscriptions = new Map(); setConnected(false, event.code !== 1000); log(`WebSocket closed: code=${event.code} reason=${event.reason || "(none)"}`, event.code === 1000 ? "info" : "error"); },
    });
    log(`CONNECT ${brokerURL} with X-Demo-User-Id=${userId || "(missing)"}`);
    client.activate();
  });

  $("disconnect").addEventListener("click", async () => { await client.deactivate(); subscriptions = new Map(); setConnected(false); log("DISCONNECTED"); });
  $("subscribe-chat").addEventListener("click", () => subscribe("chat", destination("chat"), "chat"));
  $("subscribe-events").addEventListener("click", () => subscribe("events", destination("events"), "event"));
  $("subscribe-errors").addEventListener("click", () => subscribe("errors", "/user/queue/errors", "error"));
  $("send-chat").addEventListener("click", () => {
    const trialId = $("trial-id").value.trim();
    const payload = { content: $("chat-message").value };
    const target = `/app/trials/${trialId}/chat`;
    client.publish({ destination: target, body: JSON.stringify(payload) });
    log(`SEND ${target}`, "chat", payload);
  });
  $("clear-log").addEventListener("click", () => { $("log").replaceChildren(); });
  setConnected(false);
  log("Ready. Connect, then subscribe to chat/events/errors before sending.");
})();
