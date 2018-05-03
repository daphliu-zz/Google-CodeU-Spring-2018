// Service Worker

/**
 * Caches resources that don't change often (ex. CSS, images)
 */
async function installDeps() {
    const cache = await caches.open('codeu-chat');
    return cache.addAll([
        "/css/main.css",
        "/offline.html",
    ]);
}

/**
 * @param {Request} request from user
 * @returns response from server or cache
 */
async function load(request) {
    try {
        // Attempt to load from internet
        return await fetch(request);
    } catch (err) {
        // If no internet, return from cache instead
        const response = await caches.match(request);

        if (response == null) return caches.match("/offline.html")
        else return response;
    }
}

// runs when sw first installs, which is when user first runs page 
self.addEventListener('install', event => {
    event.waitUntil(installDeps());
});

// runs when user tries to load something (ex. CSS, HTML, images)
self.addEventListener('fetch', event => {
    event.respondWith(load(event.request));
});