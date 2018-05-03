// Service Worker

async function installDeps() {
    const cache = await caches.open('mysite-static-v3');
    return cache.addAll([
        "/css/main.css"
    ])
}

self.addEventListener('install', event => {
    event.waitUntil(installDeps());
});