"use strict";

var list = [
	{ id: 1, name: 'Jennyanydots', rented: false },
	{ id: 2, name: 'Old Deuteronomy', rented: false },
	{ id: 3, name: 'Mistoffelees', rented: false }
];

function setCookie(cname, cvalue, exdays) {
  const d = new Date();
  d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
  let expires = "expires="+d.toUTCString();
  document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
  let name = cname + "=";
  let ca = document.cookie.split(';');
  for(let i = 0; i < ca.length; i++) {
    let c = ca[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
}

function getCatNum() {
	var cats = 0;
	for (var i = 0; i < list.length; i++) {
		var cat = list[i];
		if (cat.rented === false) {
			cats++;
		}
	}
	return cats;
}

function catAvailable(catName) {
	for (var i = 0; i < list.length; i++) {
		var cat = list[i];
		if (cat.name === catName && cat.rented === false) {
			return true;
		}
	}
	return false;
}
	
function listCats() {
	var listing = '<ul class="list-group">\n';
	list.forEach(function(cat, index) {
		if (cat.rented === false) {
			listing += '<li class="list-group-item">ID ' + cat.id + '. ' + cat.name + '</li>\n';
		} else {
			listing += '<li class="list-group-item">Rented out</li>\n';
		}
	})
	listing += '</ul>\n';
	document.getElementById('listing').innerHTML = listing;
}

function rentCat(id) {
	for (var i = 0; i < list.length; i++) {
		var cat = list[i];
		if (cat.id == id) {
			if (cat.rented === false) {
				cat.rented = true;
				return true;
			} else {
				// Failure
				return false;
			}
		}
	}
	return false;
}

function returnCat(id) {
	for (var i = 0; i < list.length; i++) {
		var cat = list[i];
		if (cat.id == id) {
			if (cat.rented === true) {
				cat.rented = false;
				return true;
			} else {
				// Failure
				return false;
			}
		}
	}
	return false;
}

function feedCats(catnips) {
	return catnips % getCatNum() === 0;
}

function greetCats(catName) {
	var ret = "";
	if (catName) {
		if (catAvailable(catName)) {
			ret = "Meow! from " + catName + ".";
		} else {
			ret = catName + " is not here.";
		}
	} else {
		for (var i = 0; i < getCatNum(); i++) {
			ret += "Meow!"
		}
	}
	return ret;
}

window.onload = function() {
	for (var i = 0; i < list.length; i++) {
		if (getCookie(i+1) === "true") {
			list[i].rented = true;
		}
	}
	listCats();
};

function rentSubmit() {
	var id = document.getElementById('rentID').value;
	var ret = rentCat(id);
	if (ret) {
		setCookie(id, true, 7);
		document.getElementById('rentResult').innerHTML = "Success!";
	} else {
		document.getElementById('rentResult').innerHTML = "Failure!";
	}
	document.getElementById('returnResult').innerHTML = "";
	listCats();
}

function returnSubmit() {
	var id = document.getElementById('returnID').value;
	var ret = returnCat(id);
	if (ret) {
		setCookie(id, false, 7);
		document.getElementById('returnResult').innerHTML = "Success!";
	} else {
		document.getElementById('returnResult').innerHTML = "Failure!";
	}
	document.getElementById('rentResult').innerHTML = "";
	listCats();
};

function feedSubmit() {
	var catnips = document.getElementById('catnips').value;
	var ret = feedCats(catnips);
	if (ret) {
		document.getElementById('feedResult').innerHTML = "Nom, nom, nom.";
	} else {
		document.getElementById('feedResult').innerHTML = "Cat fight!";
	}
}

function greetSubmit(catName) {
	var ret = greetCats(catName);
	document.getElementById('greeting').innerHTML = "<h4>" + ret + "</h4>";
}

function reset() {
	list.forEach(function(cat, index) {
		cat.rented = false;
		setCookie(cat.id, false, 7);
	});
	listCats();
}