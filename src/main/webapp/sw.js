// Service Worker

const CACHE_NAME = "codeu-chat"  // codeu-chat is arbitrary name of chat 

/**
 * Caches resources that don't change often (ex. CSS, images),
 * and resources needed for the service worker (ex. error pages, alternate offline versions)
 */
async function installDeps() {
    const cache = await caches.open(CACHE_NAME); 
    return cache.addAll([
        // Homepage and About are cached separately b/c the are static 
        "/css/main.css",
        "/offline.html",      // General error page
        "/?offline",          // Homepage
        "/about.jsp?offline", // About
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
        // handles conversation and CSS
        const response = await caches.match(request);          
        // if no matching cache item, return a special response
        // handles About page 
        if (response == null) return findOfflinePage(request);  
        else return response;
    }
    // Conversation is put outside b/c it updates constantly
    await storeConversation(request, response); 
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
            //returns the offline version of about "/about.jsp?offline",   
            return caches.match(url.pathname + "?offline");  
        default:
            // default displays error page 
            return caches.match("/offline.html");            
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
        // conversations is for main conversation page; 
        // chat is for individual chat which has different conversation number
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
