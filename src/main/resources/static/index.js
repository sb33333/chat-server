"use strict";

import * as Files from "./file-upload.js" ;

let opened = false;
let websocketConnection = null;

// elements
var sessionCheckButton = document.getElementById("session_check");
var connectButton = document.getElementById("connect");
var messagesDiv = document.getElementById("messages");
var messageInput = document.getElementById("messageInput");
var sendButton = document.getElementById("send");

const parseBoolean = (text) => {
  if (text === "true") return true;
  else if (text === "false") return false;
  return (text) ? true : false;
};

const fetchSessionStatus = () => {
  fetch("/chat")
    .then(res=>res.text())
    .then(isOpened => {
      console.info("session:::"+isOpened);
      opened = parseBoolean(isOpened);
      if (opened) {
        console.info("session is opened.");
      }
    })
    .catch(error => {
      console.error(error);
    });
};

const sendMessage = (message) => {
  if (!websocketConnection) {
    console.info("websocket is not opened.");
    return
  }
  websocketConnection.send(JSON.stringify({message}))
};

const sendButtonEventHandler = (event) => {
  sendMessage(messageInput.value||"");
};
const keypressEventHandler = (event) => {
  if (event.key !== "Enter") return;
  sendMessage(messageInput.value||"");
};

// setInterval(fetchSessionStatus, 1000);

const connect = () => {
  if (!opened) {
    console.info("session is not opened.");
    return;
  }

  websocketConnection = new WebSocket(`ws://${location.host}/chat`);

  websocketConnection.onmessage = (event) => {
    console.log("msg:::" + event.data);
  };

  // 연결이 열렸을 때
  websocketConnection.onopen = () => {
    console.log("Connected to chat server");
    messageInput.removeAttribute("disabled");
    sendButton.addEventListener("click", sendButtonEventHandler);
    messageInput.addEventListener("keypress", keypressEventHandler);
  };

  // 연결이 닫혔을 때
  websocketConnection.onclose = () => {
    console.log("Disconnected from chat server");
    sendButton.removeEventListener("click", sendButtonEventHandler);
    messageInput.removeEventListener("keypress", keypressEventHandler);
  };
};

sessionCheckButton.addEventListener("click", fetchSessionStatus);
connectButton.addEventListener("click", connect);


///

var fileInput = document.getElementById("upload");
var fileSubmitButton = document.getElementById("uploadSubmit");

fileSubmitButton.addEventListener("click", () => {
  Files.submit(fileInput, "/file");
});