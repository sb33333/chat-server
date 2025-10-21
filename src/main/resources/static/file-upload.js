"use strict";

const submit = (inputElement, url) => {
    const file = inputElement.files[0];
    if (!file) {
        console.error("file is not found");
        return;
    }

    const formData = new FormData();
    formData.append("file", file);

    fetch(url, {
        method: "POST",
        body: formData
    })
    .then(res => res.text())
    .then(txt => console.log(txt))
    ;
};

export {submit};