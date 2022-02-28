"use strict";

const domain = "http://localhost:8080";
let xhr;


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

function addToBucket(itemId) {
    let quantity = document.getElementById('inputQuantity');
    let url = "/api/items/add/" + itemId;
    if (quantity != null && quantity.value > 1) {
        url = url + '?quantity=' + quantity.value;
    }
    sendGet(url, () => {
        console.log('added?')
    })
}