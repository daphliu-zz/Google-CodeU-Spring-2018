// Service Worker

const CACHE_NAME = "codeu-chat"  // codeu-chat is arbitrary name of chat 

/**
 * Caches resources that don't change often (ex. CSS, images),
 * and resources needed for the service worker (ex. error pages, alternate offline versions)
 */
async function installDeps() {
    const cache = await caches.open(CACHE_NAME); 
    return cache.addAll([
        "/css/main.css",
        "/offline.html", // General error page
        "/?offline", // Homepage
        "/about.jsp?offline", // About
        // Homepage and About are cached separately b/c the are static 
    ]);
}

/**
 * Called to load a response from the network or cache.
 * @param {Request} request from user
 * @returns response from server or cache
 */
async function load(request) {
    let response;
    try {
        response = await fetch(request);
    } catch (err) { 
        // If no internet, return from cache instead
        const response = await caches.match(request);           // handles conversation and CSS
        // if no matching cache item, return a special response
        if (response == null) return findOfflinePage(request);  // handles About page 
        else return response;
    }
    await storeConversation(request, response); // Conversation is put outside b/c it updates constantly
    return response;
}

/**
 * Find a version of the page to return for offline users. 
 * Otherwise, return an error page.
 * @param {Request} request from user
 * @returns response from cache
 */
async function findOfflinePage(request) {
    const url = new URL(request.url);
    switch (url.pathname) {
        case "/":
        case "/about.jsp":                                      
            return caches.match(url.pathname + "?offline");  //returns the offline version of about "/about.jsp?offline",
        default:
            return caches.match("/offline.html");            // default displays error page 
    }
}

/**
 * If the request was for a conversation page, clone the response and store
 * it in the cache.
 * @param {Request} request from user
 * @param {Response} response from server
 * @returns {Promise<void>}
 */
async function storeConversation(request, response) {
    const url = new URL(request.url);

    if (url.pathname === "/conversations" || url.pathname.startsWith("/chat")) {
        // conversations is for main conversation page; /chat is for individual chat which has different conversation number
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
