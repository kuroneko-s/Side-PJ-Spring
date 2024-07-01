// 플로팅 메시지 띄우기
function floatMessage(message) {
    document.getElementById("toast-message-context").textContent = message;
    const containerEl = document.getElementById("toast-message-container");
    containerEl.classList.remove("hide");

    // 3초 후 자동 종료
    setTimeout(() => containerEl.classList.add("hide"), 3000);
}

// fetch 전송
function sendFetch(url, method, body, callback, errorCallback) {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch(url, {
        method: method,
        body: JSON.stringify(body),
        headers: {
            [csrfHeader]: csrfToken,
            "Content-Type": "application/json; charset=utf-8"
        }
    }).then(res => res.text())
        .then(text => {
            callback && callback(text);
        })
        .catch(err => {
            errorCallback && errorCallback(err);
            console.error(err);
        });
}

// 클래스 존재 유무 재귀함수
function hasClass(el, className) {
    if (el.classList.contains("container")) return [false, el];
    else if (el.classList.contains(className)) return [true, el];

    return hasClass(el.parentNode, className);
}