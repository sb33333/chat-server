"use strict";

const FILE_URL = window.location.origin + "/file_b";

const upload = (inputElement) => {
    const file = inputElement.files[0];
    if (!file) {
        console.error("file is not found");
        return;
    }

    const formData = new FormData();
    formData.append("file", file);

    return fetch(FILE_URL, {
        method: "POST",
        body: formData
    })
    .then(res => res.json())
    ;
};

const download = (id) => {
    
};

export {upload};