/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


// Add more fields dynamically.
function addField(field1, field2,area) {
    if(!document.getElementById) return; //Prevent older browsers from getting any further.
    var field_area = document.getElementById(area);
    var titleCounter = document.getElementById("titleCounter");
    var count = 0;
    count = titleCounter.getAttribute("value");
    count++;
    titleCounter.setAttribute("value", count);
    //If the maximum number of elements have been reached, exit the function.
    //		If the given limit is lower than 0, infinite number of fields can be created.

    var div = document.createElement("div");
    var nameLabel = document.createElement("label");
    var representativeLabel = document.createElement("label");
    var name = document.createElement("input");
    var representative = document.createElement("input");
    // div
    div.setAttribute("class", "titleInput");
    // label name
    nameLabel.htmlFor = field1+count;
    nameLabel.textContent = "Název: ";
    // name input tag
    name.id = field1+count;
    name.name = field1+count;
    name.type = "text"; //Type of field - can be any valid input type like text,file,checkbox etc.
   
    representativeLabel.htmlFor = field2+count;
    representativeLabel.textContent = " Hlavní p\u0159edstavitel: ";

    representative.id = field2+count;
    representative.name = field2+count;
    representative.type = "text"; //Type of field - can be any valid input type like text,file,checkbox etc.


    div.appendChild(nameLabel);
    div.appendChild(name);
    div.appendChild(representativeLabel);
    div.appendChild(representative);
    div.appendChild(document.createElement("br"));
    field_area.appendChild(div);

}

// Dynamicly remove items from a form
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

// Validating add dvd input form
function validateForm(){

    var name=document.getElementById("dvdName").value;
    var counter = document.getElementById("titleCounter").getAttribute("value");
    
    if (name == ""){
        alert("Jméno dvd musí být vypln\u011bno");
        document.getElementById("dvdName").focus();
        return false;
    }
    if(document.getElementById("titleCounter").value == 0){
        alert("Na dvd musí být alespo\u0148 jeden titul!");
        return false;
    }
    var i = 1;
    while(i <= counter){
        var titleName = document.getElementById("titleName_"+i).value;
        if (titleName == ""){
            alert("Musíte zadat jméno u titulu \u010d. "+i);
            document.getElementById("titleName_"+i).focus();
            return false;
        }
        i++;
    }
    
    return true;
}

// Confirm deletion dialog
function confirmDelete(id){
    var dvdName = document.getElementById("dvd"+id).textContent;
    
    return(confirm("Chcete opravdu smazat "+dvdName));
}