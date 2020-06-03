async function init() {

    const registration = await navigator.serviceWorker.register('/sw.js');
    await navigator.serviceWorker.ready;
    firebase.initializeApp({
        apiKey: "AIzaSyDjj7jEYX1U37ZLt0VmpaxxzhAAZKSnrfk",
        projectId: "web-push-notification-78fef",
        messagingSenderId: "188887584064",
        appId: "1:188887584064:web:ecf8a3d2f2d2c7909ebea5"
    });
    const messaging = firebase.messaging();
    messaging.usePublicVapidKey('BIs0dGYVdHmrf52hYhzdVxyUkcIPqTbCLWMCyzg-pWqq-COfYn5cTwpPiYVPWQRbDCxlWtvX2hQ461dgVaaYI2Y');
    messaging.useServiceWorker(registration);

    try {
        await messaging.requestPermission();
    } catch (e) {
        console.log('Unable to get permission', e);
        return;
    }

    navigator.serviceWorker.addEventListener('message', event => {
        if (event.data === 'newData') {
            showData();
        }
    });

    const currentToken = await messaging.getToken();
    fetch('/register', {method: 'post', body: currentToken});
    showData();

    messaging.onTokenRefresh(async () => {
        console.log('token refreshed');
        const newToken = await messaging.getToken();
        fetch('/register', {method: 'post', body: currentToken});
    });

}

async function showData() {
    const db = await getDb();
    const tx = db.transaction('jokes', 'readonly');
    const store = tx.objectStore('jokes');
    store.getAll().onsuccess = e => showJokes(e.target.result);
}

function showJokes(jokes) {
    const table = document.getElementById('outTable');

    jokes.sort((a, b) => parseInt(b.ts) - parseInt(a.ts));
    const html = [];
    jokes.forEach(j => {
        const date = new Date(parseInt(j.ts));
        html.push(`<div><div class="header">${date.toISOString()} ${j.id} (${j.seq})</div><div class="joke">${j.joke}</div></div>`);
    });
    table.innerHTML = html.join('');
}

async function getDb() {
    if (this.db) {
        return Promise.resolve(this.db);
    }
    return new Promise(resolve => {
        const openRequest = indexedDB.open("Chuck", 1);

        openRequest.onupgradeneeded = event => {
            const db = event.target.result;
            db.createObjectStore('jokes', {keyPath: 'id'});
        };

        openRequest.onsuccess = event => {
            this.db = event.target.result;
            resolve(this.db);
        }
    });
}

init();