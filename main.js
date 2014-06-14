document.onreadystatechange = function() {
	var stepTittles = document.querySelectorAll('[id^=step]');
	for (var i = 0; i < stepTittles.length; i++) {
		var stepTittle = stepTittles[i];
		addClass(stepTittle.parentNode.getElementsByClassName(stepTittle.id)[0], 'closed');
		stepTittle.addEventListener("click",toggleStep);
	}
	var dropDownButtons = document.getElementsByClassName('download');
	for (var i = 0; i < dropDownButtons.length; i++) {
		var dropDownButton = dropDownButtons[i];
		dropDownButton.addEventListener("click",toggleDropDownList);
	}
}

function toggleStep(){
	var objToOpen = this.parentNode.getElementsByClassName(this.id)[0];
	if(hasClass(objToOpen, "closed")){
		removeClass(objToOpen,'closed');
		addClass(objToOpen, "open");
	} else {
		removeClass(objToOpen, 'open');
		addClass(objToOpen, 'closed');
	}
};

function toggleDropDownList(){
	var dropDownList = this.parentNode.parentNode.getElementsByClassName('dropDownList')[0];
	if(hasClass(dropDownList, 'closed')){
		removeClass(dropDownList,'closed');
		addClass(dropDownList, "open");
	} else {
		removeClass(dropDownList, 'open');
		addClass(dropDownList, 'closed');
	}
};

function hasClass(element, klass) {
    var classNames = element.className.split(' ');
    for (var i = 0; i < classNames.length; i++) {
        if (classNames[i].toLowerCase() == klass.toLowerCase()) {
            return true;
        }
    }
    return false;
}

function addClass(element, klass){
	if (!hasClass(element, klass))
		element.className += ' ' + klass
}

function removeClass(element, klass){
	element.className = element.className.replace(klass, '').trim();
}