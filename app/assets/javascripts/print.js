document.addEventListener("DOMContentLoaded", function () {
   const printLinks = document.querySelectorAll(".print-link");
   printLinks.forEach(function (printLink) {
       printLink.addEventListener("click", function (e) {
           e.preventDefault();
           window.print();
       });
   });
});