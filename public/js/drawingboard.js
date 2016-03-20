// reference: http://www.williammalone.com/articles/create-html5-canvas-javascript-drawing-app/#demo-complete

var canvas;
var context;
var canvasWidth = 280;
var canvasHeight = 280;
var paint = false;
var clickX = new Array();
var clickY = new Array();
var clickDrag = new Array();

function prepareCanvas() {
  var canvasDiv = document.getElementById('drawingboard');
  canvas = document.createElement('canvas');
  canvas.setAttribute('width', canvasWidth);
  canvas.setAttribute('height', canvasHeight);
  canvas.setAttribute('id', 'canvas');
  canvasDiv.appendChild(canvas);
  context = canvas.getContext("2d");

  $('#canvas').mousedown(function(e){
    var mouseX = e.pageX - this.offsetLeft;
    var mouseY = e.pageY - this.offsetTop;

    paint = true;
    addClick(e.pageX - this.offsetLeft, e.pageY - this.offsetTop);
    redraw();
  });

  $('#canvas').mousemove(function(e){
    if(paint==true){
      addClick(e.pageX - this.offsetLeft, e.pageY - this.offsetTop, true);
      redraw();
    }
  });

  $('#canvas').mouseup(function(e){
    paint = false;
    redraw();
  });

  $('#canvas').mouseleave(function(e){
    paint = false;
  });
}

function addClick(x, y, dragging)
{
  clickX.push(x);
  clickY.push(y);
  clickDrag.push(dragging);
}

function redraw(){
  context.clearRect(0, 0, context.canvas.width, context.canvas.height); // Clears the canvas

  context.strokeStyle = "#ffffff";
  context.lineJoin = "round";
  context.lineWidth = 20;

  for(var i=0; i < clickX.length; i++) {
    context.beginPath();
    if(clickDrag[i] && i){
      context.moveTo(clickX[i-1], clickY[i-1]);
    }else{
      context.moveTo(clickX[i]-1, clickY[i]);
    }
    context.lineTo(clickX[i], clickY[i]);
    context.closePath();
    context.stroke();
  }
}

function clearCanvas() {
  context.clearRect(0, 0, canvasWidth, canvasHeight);
  clickX = new Array();
  clickY = new Array();
  clickDrag = new Array();
}

function getImage() {
  var imgDataURL = canvas.toDataURL();
  return drawDataURIOnCanvas(imgDataURL, 112, 112, "showSavedImg");
  //console.log(drawDataURIOnCanvas(imgDataURL, 28, 28, "showSmallImg"));
}

function drawDataURIOnCanvas(imgDataURL, width, height, id) {
  var canvasDiv = document.getElementById(id);
  var newCanvas = document.createElement('canvas');
  newCanvas.setAttribute('width', width+'px');
  newCanvas.setAttribute('height', height+'px');
  newCanvas.setAttribute('id', 'canvas'+id);
  var oldCanvas = document.getElementById('canvas'+id);
  if (oldCanvas != null) {
    canvasDiv.removeChild(oldCanvas);
  }
  canvasDiv.appendChild(newCanvas);
  var ctx = newCanvas.getContext('2d');

  var img = new Image();
  img.src = imgDataURL
  img.onload = function () {
    ctx.drawImage(img,0,0, width, height);
  }
  //document.getElementById(id).value = imgDataURL;
  //return document.getElementById('canvas'+id).toDataURL("image/png").toString();
  return imgDataURL;
}
