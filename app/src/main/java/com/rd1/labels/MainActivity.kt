package com.rd1.labels

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            builtInZoomControls = false
            displayZoomControls = false
            allowFileAccess = false
            allowContentAccess = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }

        webView.loadDataWithBaseURL(
            "https://app.local/",
            html,
            "text/html",
            "utf-8",
            null
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    private val html = """
        <!doctype html>
        <html lang="ru">
        <head>
          <meta charset="UTF-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0, viewport-fit=cover" />
          <title>RD1 Labels</title>
          <style>
            :root{
              --bg:#0b0b0f;
              --panel:#17171d;
              --line:#2b2b34;
              --text:#ffffff;
              --muted:#9aa0aa;
              --accent:#ff9f1a;
            }
            *{box-sizing:border-box}
            body{margin:0;background:var(--bg);color:var(--text);font-family:Arial,sans-serif}
            .app{padding:16px;max-width:700px;margin:0 auto}
            .card{background:var(--panel);border:1px solid var(--line);border-radius:18px;padding:16px;margin-bottom:14px}
            .title{font-size:24px;font-weight:700;margin:0 0 8px 0}
            .muted{color:var(--muted);font-size:13px}
            .label{font-size:14px;margin-bottom:8px}
            input,button{width:100%;border-radius:12px;padding:12px;border:none;font-size:15px}
            input{background:#101017;color:#fff;border:1px solid #33343d}
            button{background:var(--accent);color:#111;font-weight:700;cursor:pointer}
            .row{display:grid;grid-template-columns:1fr 1fr;gap:10px;margin-top:10px}
            .rail{display:grid;grid-template-columns:repeat(4,1fr);gap:10px}
            .module{background:#101017;border:1px dashed #3a3a45;border-radius:12px;padding:14px 8px;text-align:center;font-weight:700}
            .list{display:flex;flex-direction:column;gap:10px;margin-top:10px}
            .item{background:#101017;border:1px solid #2d2f38;border-radius:12px;padding:12px}
          </style>
        </head>
        <body>
          <div class="app">
            <div class="card">
              <div class="title">RD1 Labels</div>
              <div class="muted">Android prototype</div>
            </div>

            <div class="card">
              <div class="label">Название проекта</div>
              <input id="projectName" value="Щит квартиры" />
              <div class="row">
                <button onclick="saveProject()">Сохранить</button>
                <button onclick="exportProjects()">Экспорт JSON</button>
              </div>
            </div>

            <div class="card">
              <div class="label">DIN-рейка</div>
              <div class="rail">
                <div class="module">QF1</div>
                <div class="module">QF2</div>
                <div class="module">УЗО</div>
                <div class="module">Реле</div>
              </div>
            </div>

            <div class="card">
              <div class="label">Проекты</div>
              <div id="projects" class="list"></div>
            </div>
          </div>

          <script>
            const KEY = "rd1-projects";

            function loadProjects() {
              try { return JSON.parse(localStorage.getItem(KEY) || "[]"); }
              catch (e) { return []; }
            }

            function saveProjectsToStorage(items) {
              localStorage.setItem(KEY, JSON.stringify(items));
            }

            function render() {
              const wrap = document.getElementById("projects");
              const items = loadProjects();

              if (!items.length) {
                wrap.innerHTML = '<div class="item"><div>Пока нет проектов</div><div class="muted">Нажми "Сохранить"</div></div>';
                return;
              }

              wrap.innerHTML = items.map(p => `
                <div class="item">
                  <div><strong>${p.name}</strong></div>
                  <div class="muted">${p.updatedAt}</div>
                </div>
              `).join("");
            }

            function saveProject() {
              const name = document.getElementById("projectName").value || "Новый проект";
              const items = loadProjects();
              items.unshift({
                id: String(Date.now()),
                name,
                updatedAt: new Date().toLocaleString()
              });
              saveProjectsToStorage(items);
              render();
            }

            function exportProjects() {
              const items = loadProjects();
              const blob = new Blob([JSON.stringify(items, null, 2)], { type: "application/json" });
              const url = URL.createObjectURL(blob);
              const a = document.createElement("a");
              a.href = url;
              a.download = "rd1-projects.json";
              a.click();
              URL.revokeObjectURL(url);
            }

            render();
          </script>
        </body>
        </html>
    """.trimIndent()
}
