"use strict";

const FILE_URL = window.location.origin + "/file_b";

export const upload = async (file) => {
    if (!file) throw new Error("file is not found");

    const formData = new FormData();
    formData.append("file", file);

    const res = await fetch(FILE_URL, {
        method: "POST",
        body: formData
    });
    return await res.json();
};

export const download = async (id) => {
    const res = await fetch(FILE_URL + "?id=" + id, {method: "GET"});
    return await res.blob();
};
