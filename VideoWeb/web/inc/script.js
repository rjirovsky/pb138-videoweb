/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


//Add more fields dynamically.
function addField(field1, field2,area,limit) {
    if(!document.getElementById) return; //Prevent older browsers from getting any further.
    var field_area = document.getElementById(area);
    var titleCounter = document.getElementById("titleCounter");
    var count = 0;
    count = titleCounter.getAttribute("value");
    count++;
    titleCounter.setAttribute("value", count);
    //If the maximum number of elements have been reached, exit the function.
    //		If the given limit is lower than 0, infinite number of fields can be created.
    if(count > limit && limit > 0) return;

    if(document.createElement) { //W3C Dom method.
        var div = document.createElement("div");
        var nameLabel = document.createElement("label");
        var representativeLabel = document.createElement("label");
        var name = document.createElement("input");
        var representative = document.createElement("input");
        // div
        div.setAttribute("class", "titleInput");
        // label name
        nameLabel.htmlFor = field1+count;
        nameLabel.innerText = "Název: ";
        // name input tag
        name.id = field1+count;
        name.name = field1+count;
        name.type = "text"; //Type of field - can be any valid input type like text,file,checkbox etc.

        representativeLabel.htmlFor = field2+count;
        representativeLabel.innerText = " Hlavní p\u0159edstavitel: ";

        representative.id = field2+count;
        representative.name = field2+count;
        representative.type = "text"; //Type of field - can be any valid input type like text,file,checkbox etc.


        div.appendChild(nameLabel);
        div.appendChild(name);
        div.appendChild(representativeLabel);
        div.appendChild(representative);
        div.appendChild(document.createElement("br"));
        field_area.appendChild(div);

    } else { //Older Method
            field_area.innerHTML += "<li><input name='"+(field1+count)+"' id='"+(field1+count)+"' type='text' /></li>";
    }
}

// fucntion to dynamicly remove items from a form
function removeField(){
    if(!document.getElementById) return;
    
    var titleCounter = document.getElementById("titleCounter");
    var counter = 0;
    counter = titleCounter.getAttribute("value");
    
    var fieldSet = document.getElementById("titles");
    var someField = fieldSet.getElementsByTagName("div");
    fieldSet.removeChild(someField[counter-1]);
    counter--
    titleCounter.setAttribute("value", counter);
    
    
}