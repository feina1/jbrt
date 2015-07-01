var margin = {top: 10, right: 120, bottom: 5, left: 420},
    width = 1200 - margin.right - margin.left,
    height = 650 - margin.top - margin.bottom;
    
var i = 0,
    duration = 750;

var tree = d3.layout.tree()
    .size([height, width]);

var movieInfoDiv = d3.select("#movieInfo");
var lineTextDiv = d3.select("#lineText");


var diagonal = d3.svg.diagonal()
    .projection(function(d) { return [d.y, d.x]; });

//line
var line = d3.svg.line();

var svg = d3.select("body").append("svg")
	.attr("width", width + margin.right + margin.left)
	.attr("height", height + margin.top + margin.bottom)
  .append("g")
  	.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

var currentData;

d3.json("model.json", function(error, flare) {
	createTreeButton(flare.trees.length);
	currentData = flare;
	var root = trans(flare.trees[0]);
	root.x0 = height / 2;
	root.y0 = 0;
	root.children.forEach(collapse);
	update(root,root);
	d3.select("body").append("div").style("margin-left","200px").html("Top Node: True");
	d3.select("body").append("div").style("margin-left","200px").html("Bottom Node: False");

  function collapse(d) {
    if (d.children) {
      d._children = d.children;
      d._children.forEach(collapse);
      d.children = null;
    }
  }
  
});

d3.select(self.frameElement).style("height", "800px");

function createTreeButton(cnt){
	for(var i = 1;i<cnt+1;i++){
		var $button = $("<button value='tree"+cnt+"'"+" onclick=changeTree("+i+")>Tree"+i+"</button>");
		$("#buttons").append($button);
	}
}

function changeTree(index){
	var tmp=index;
	var root = trans(currentData.trees[index-1]);
	root.x0 = height / 2;
	root.y0 = 0;
	root.children.forEach(collapse);
	update(root,root);

  function collapse(d) {
    if (d.children) {
      d._children = d.children;
      d._children.forEach(collapse);
      d.children = null;
    }
  }
}

function trans(json){
	var node = {};
	var numJson = fixedNum(json);
	node.featureIndex = json.featureIndex;
	node.featureName = json.featureName;
	node.splitValue = numJson.splitValue;
	node.trueDelta = numJson.trueDelta;
	node.falseDelta = numJson.falseDelta;
	node.loss = numJson.loss;
	node.avgTrueValue = numJson.avgTrueValue;
	node.sampleNum = numJson.sampleNum;
	
	var childrenArray = new Array();
	if(json.trueChild != null){
		childrenArray.push(trans(json.trueChild));
	}
	if(json.falseChild != null){
		childrenArray.push(trans(json.falseChild))
	}
	node.children = childrenArray;
	return node;
}

function fixedNum(json){
	var numJson = {};
	if(!isNaN(json.splitValue)){
		numJson.splitValue = dealNum(json.splitValue);
	}
	if(!isNaN(json.trueDelta)){
		numJson.trueDelta = dealNum(json.trueDelta);
	}
	if(!isNaN(json.falseDelta)){
		numJson.falseDelta = dealNum(json.falseDelta);
	}
	if(!isNaN(json.loss)){
		numJson.loss = dealNum(json.loss);
	}
	if(!isNaN(json.avgTrueValue)){
		numJson.avgTrueValue = dealNum(json.avgTrueValue);
	}
	if(!isNaN(json.sampleNum)){
		numJson.sampleNum = dealNum(json.sampleNum);
	}
	return numJson;
}


function dealNum(oldNum){
	var newNum;
	var temp = oldNum + "";
	if(temp.indexOf(".")!=-1 && temp.split(".")[1].length > 1){
		newNum = oldNum.toFixed(2);
	}else{
		newNum = oldNum;
	}
	return newNum;
}
function update(root,source) {

  // Compute the new tree layout.
  var nodes = tree.nodes(root).reverse(),
      links = tree.links(nodes);

  // Normalize for fixed-depth.
  nodes.forEach(function(d) { 
	  d.y = d.depth * 180; 
	  });

  // Update the nodes…
  var node = svg.selectAll("g.node")
      .data(nodes, function(d) {return d.id || (d.id = ++i); });

  // Enter any new nodes at the parent's previous position.
  var nodeEnter = node.enter().append("g")
      .attr("class", "node")
      .attr("transform", function(d) { 
    	  return "translate(" + source.y0 + "," + source.x0 + ")"; 
    	  })
      .on("click", click);

//Toggle children on click.
  function click(d) {
	svg.selectAll(".linetext").style("fill-opacity",0.0);
    if (d.children) {
      d._children = d.children;
      d.children = null;
    } else {
      d.children = d._children;
      d._children = null;
    }
    update(root,d);
  }
  
  nodeEnter.append("circle")
      .attr("r", 1e-6)
      .style("fill", function(d) { 
    	  return d._children ? "lightsteelblue" : "#fff"; 
    	  });

  nodeEnter.append("text")
      .attr("x", function(d) { 
    	  return d.children || d._children ? -10 : 10; 
    	  })
      .attr("dy", ".35em")
      .attr("text-anchor", function(d) { 
    	  return d.children || d._children ? "end" : "start"; 
    	  })
      .text(function(d) { 
    	  if(d.featureName == null)
    		  return "";
    	  else
    		  return d.featureName + " < "+ d.splitValue; 
    	  })
      .style("fill-opacity", 1e-6);

	
  nodeEnter.on("mouseover", showMoviePanel).on("mouseout", closeMoviePanel);
  
  // Transition nodes to their new position.
  var nodeUpdate = node.transition()
      .duration(duration)
      .attr("transform", function(d) { 
    	  return "translate(" + d.y + "," + d.x + ")"; 
    	  });

  nodeUpdate.select("circle")
      .attr("r", 4.5)
      .style("fill", function(d) { 
    	  return d._children ? "lightsteelblue" : "#fff"; 
    	  });

  nodeUpdate.select("text")
      .style("fill-opacity", 1);

  // Transition exiting nodes to the parent's new position.
  var nodeExit = node.exit().transition()
      .duration(duration)
      .attr("transform", function(d) { 
    	  return "translate(" + source.y + "," + source.x + ")"; 
    	  })
      .remove();

  nodeExit.select("circle")
      .attr("r", 1e-6);

  nodeExit.select("text")
      .style("fill-opacity", 1e-6);

  /*var linkText = svg.selectAll(".linetext")
  	  .data(links)
  	  .enter()
  	  .append("text")
  	  .attr("class","linetext")
  	  .style("fill-opacity",1.0)
  	  .attr("y",function(d){
  		  return ((d.source.x + d.target.x)/2);
  	  })
  	  .attr("x",function(d){
  		  return ((d.source.y + d.target.y)/2);
  	  })
  	  .text(function(d){
  		  return d.target == d.source.children[0] ? "T" : "F";
  	  })*/
  
  // Update the links…
  var link = svg.selectAll("path.link")
      .data(links, function(d) { 
    	  return d.target.id; 
    	  });

  // Enter any new links at the parent's previous position.
  link.enter().insert("path", "g")
      .attr("class", "link")
      .attr("d", function(d) {
        var o = {x: source.x0, y: source.y0};
        return diagonal({source: o, target: o});
      });
 
  // Transition links to their new position.
  link.transition()
      .duration(duration)
      .attr("d", diagonal);

  // Transition exiting nodes to the parent's new position.
  link.exit().transition()
      .duration(duration)
      .attr("d", function(d) {
        var o = {x: source.x, y: source.y};
        return diagonal({source: o, target: o});
      })
      .remove();

  // Stash the old positions for transition.
  nodes.forEach(function(d) {
    d.x0 = d.x;
    d.y0 = d.y;
  });
}

/* --------------------------------------------------------------------- */
/* Show the movie details panel for a given node
*/
function showMoviePanel( d ) {
if (d3.event!=null&&d3.event.defaultPrevented) 
	  return; //ignore drag
	movieInfoDiv.html( getMovieInfo(d)).attr("class","panel_on");
	//move the info box
	var x = d.x-100;
	var y = d.y+300;
	movieInfoDiv.style("left",y+"px").style("top",x+"px");
}

function closeMoviePanel(d){
	movieInfoDiv.html( getMovieInfo(d)).attr("class","panel_off");
}

function getMovieInfo( n ) {
    //info = '<div class=f><span class=l>featureIndex</span>: <span class=g>'+n.featureIndex+'</span></div>';
    //info = '<div class=f><span class=l>featureTitle</span>: <span class=g>'+n.featureTitle+'</span></div>';
    //info += '<div class=f><span class=l>splitValue</span>: <span class=g>'+n.splitValue+'</span></div>';
	info = '<div class=f><span class=l>avgTrueValue</span>: <span class=g>'+n.avgTrueValue+'</span></div>';
    info += '<div class=f><span class=l>trueDelta</span>: <span class=g>'+n.trueDelta+'</span></div>';
    info += '<div class=f><span class=l>falseDelta</span>: <span class=g>'+n.falseDelta+'</span></div>';
    info += '<div class=f><span class=l>loss</span>: <span class=g>'+n.loss+'</span></div>';
    info += '<div class=f><span class=l>sampleNum</span>: <span class=g>'+n.sampleNum+'</span></div>';
    return info;
}
