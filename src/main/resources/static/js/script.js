"use strict";

const domain = "http://localhost:8080";
let xhr;

//todo Пересмотреть код и избавить от бессмысленого импортирования в каждый файл

function sendGet(url, f) {
    xhr = new XMLHttpRequest();
    xhr.open('GET', domain + url)
    xhr.onload = f;
    xhr.send();
}

function ref(url) {
    let anchor = document.createElement('a');
    anchor.href = domain + url;
    anchor.click();
}

function sendPatch(url, f) {
    xhr = new XMLHttpRequest();
    xhr.open('PATCH', domain+url);
    xhr.onload = f;
    xhr.send();
}

function sendDelete(url, f) {
    xhr = new XMLHttpRequest()
    xhr.open("DELETE", domain+url);
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

    // sendGet(url, () => {
    //     console.log('added?')
    // })
}