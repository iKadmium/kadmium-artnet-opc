"use strict";
let webSocket;
let imageData;
let slider; 

let pixelCanvas;
let pixelCtx;

let zoomedCanvas;
let zoomedCtx;

let scale;

function onMessage(event)
{
    let message = JSON.parse(event.data);
    let i = 0;
    let j = 0;
    for(let i = 0; i < message.length; i += 3)
    {
        imageData.data[j] = message[i] & 255;
        imageData.data[j + 1] = message[i + 1] & 255;
        imageData.data[j + 2] = message[i + 2] & 255;
        imageData.data[j + 3] = 255; // alpha
        j += 4;
    }
    pixelCtx.putImageData(imageData, 0, 0);
    let scaledImageData = scaleImageData(imageData, scale);
    zoomedCtx.putImageData(scaledImageData, 0, 0);
}

function scaleImageData(imageData, scale) {
  var scaled = zoomedCtx.createImageData(imageData.width * scale, imageData.height * scale);

  for(var row = 0; row < imageData.height; row++) {
    for(var col = 0; col < imageData.width; col++) {
      var sourcePixel = [
        imageData.data[(row * imageData.width + col) * 4 + 0],
        imageData.data[(row * imageData.width + col) * 4 + 1],
        imageData.data[(row * imageData.width + col) * 4 + 2],
        imageData.data[(row * imageData.width + col) * 4 + 3]
      ];
      for(var y = 0; y < scale; y++) {
        var destRow = row * scale + y;
        for(var x = 0; x < scale; x++) {
          var destCol = col * scale + x;
          for(var i = 0; i < 4; i++) {
            scaled.data[(destRow * scaled.width + destCol) * 4 + i] =
              sourcePixel[i];
          }
        }
      }
    }
  }

  return scaled;
}

function onSlide(event)
{
    scale = Math.round(slider.value);
    //zoomedCtx.scale(scale, scale);
    zoomedCanvas.height = yCount * scale;
    zoomedCanvas.width = xCount * scale;
}

function onLoad()
{
    slider = document.getElementById("zoomSlider");
    
    zoomedCanvas = document.getElementById("zoomedCanvas");
    zoomedCtx = zoomedCanvas.getContext("2d");
    
    pixelCanvas = document.getElementById("pixelCanvas");
    pixelCtx = pixelCanvas.getContext("2d");
    imageData = pixelCtx.createImageData(xCount, yCount);
    webSocket = new WebSocket(websocketAddress);
    webSocket.onmessage = onMessage;
    
    slider.oninput = onSlide;
    scale = 1;
    
    pixelCtx.fillStyle = "black"; // this is default anyway
    pixelCtx.fillRect(0, 0, pixelCanvas.width, pixelCanvas.height);
    zoomedCtx.fillRect(0, 0, zoomedCanvas.width, zoomedCanvas.height);
}

window.addEventListener("load", onLoad);