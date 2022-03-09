"use strict";
let xhr;

function sendGet(url, f) {
    xhr = new XMLHttpRequest();
    xhr.open('GET', origin + url)
    xhr.onload = f;
    xhr.send();
}

function ref(url) {
    let anchor = document.createElement('a');
    anchor.href = origin + url;
    anchor.click();
}

function sendPatch(url, f) {
    xhr = new XMLHttpRequest();
    xhr.open('PATCH', origin+url);
    xhr.onload = f;
    xhr.send();
}

function sendDelete(url, f) {
    xhr = new XMLHttpRequest()
    xhr.open("DELETE", origin+url);
    xhr.onload = f;
    xhr.send();
}

function addToBucket(itemId) {
    let quantity = document.getElementById('inputQuantity');
    let url = "/api/bucket/" + itemId;
    if (quantity != null && quantity.value > 1) {
        url = url + '?quantity=' + quantity.value;
    }
    sendPatch(url, () => console.log(xhr.response))
}

function createElementByTagAndClassName(tag, className) {
    let element = document.createElement(tag);
    element.className = className;
    return element;
}

function loadNav(page, fPrev, fTo, fNext) {
    let pageNav = document.getElementById('pageable');
    let liPrev = document.createElement('li');
    liPrev.id = 'prev';
    if (page.first) {
        liPrev.className = 'page-item disabled';
    } else {
        liPrev.className = 'page-item';
    }
    let prevButton = document.createElement('button');
    prevButton.className = 'page-link';
    prevButton.innerText = 'Prev';
    prevButton.onclick = fPrev;
    liPrev.append(prevButton);
    pageNav.append(liPrev);

    for (let i = 0; i < page.totalPages; i++) {
        let liNumPage = document.createElement('li');
        let btnPage = document.createElement('button');
        liNumPage.className = 'page-item';
        btnPage.className = 'page-link';
        btnPage.innerHTML = (i + 1).toString();
        btnPage.onclick = fTo;
        if (i === page.number) {
            liNumPage.className = liNumPage.className + ' active';
        }
        liNumPage.appendChild(btnPage);
        pageNav.append(liNumPage);
    }
    let liNext = document.createElement('li');
    liNext.id = 'next';
    if (page.last) {
        liNext.className = 'page-item disabled';
    } else {
        liNext.className = 'page-item';
    }
    let nextButton = document.createElement('button');
    nextButton.className = 'page-link'
    nextButton.innerText = 'Next';
    nextButton.onclick = fNext;
    liNext.append(nextButton);
    pageNav.append(liNext);
}