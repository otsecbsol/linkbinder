
 function findFirstElement() {
     var elements = document.all ? document.all : document.getElementsByTagName('*');
     for (var i = 0, len = elements.length; i < len; i++) {
         if (elements[i].type != 'hidden' 
             && elements[i].type != 'button'
             && elements[i].type != 'submit'
             && !elements[i].disabled
             && ['input', 'select', 'textarea'].include(elements[i].tagName.toLowerCase())
             && elements[i].style.display != 'none'
             && !elements[i].readOnly) {
 
             return elements[i];
         }
     }
     return null;
 }
 /* 
  * Focus the first element.
  */
 var func = function() {
   var top = document.body.scrollTop;
   if (top > 0) {
       //  Do nothing
   } else {
       var first = findFirstElement();
       if (first) {
           try {
               first.focus();
           } catch (e) {}
       }
   }
   clearTimeout(timerId);
 };
 var timerId = setTimeout(func, 200);
