"use strict";

function loadBucket() {
    sendGet("/api/bucket", () => {
        console.log('sendGet processed')
    })

}