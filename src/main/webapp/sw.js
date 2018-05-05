// Service Worker

const CACHE_NAME = "codeu-chat"

/**
 * Caches resources that don't change often (ex. CSS, images)
 */
async function installDeps() {
    const cache = await caches.open(CACHE_NAME);  // codeu-chat is arbitrary name of chat 
    return cache.addAll([
        "/css/main.css",
        "/offline.html", // General error page
        "/?offline", // Homepage
        "/about.jsp?offline", // About
    ]);
}

/**
 * @param {Request} request from user
 * @returns response from server or cache
 */
async function load(request) {
    try {
        // Attempt to load from internet
        const response = await fetch(request);
        await storeConversation(request, response);  // Conversation is put in try block b/c it updates constantly
        return response;
    } catch (err) {
        // If no internet, return from cache instead
        const response = await caches.match(request); 
        // expect only /css/main.css match 
        if (response == null) return findOfflinePage(request);
        else return response;
    }
}

/**
 * @param {Request} request from user
 * @returns response from cache
 */
async function findOfflinePage(request) {
    const url = new URL(request.url);
    switch (url.pathname) {
        case "/":
        case "/about.jsp":
            return caches.match(url.pathname + "?offline");
        default:
            return caches.match("/offline.html");
    }
}

/**
 * If the request was for a conversation page, clone the response and store
 * it in the cache.
 * @param {Request} request from user
 * @param {Response} response from server
 */
async function storeConversation(request, response) {
    const url = new URL(request.url);

    if (url.pathname === "/conversations" || url.pathname.startsWith("/chat")) {
        const cache = await caches.open(CACHE_NAME)
        cache.put(request, response.clone());
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