const search = () => {
     let query = $("#search-input").val();
     if (query == "") {
          $(".search-result").hide();
     }
     else {
          let url = `http://localhost:8080/search/${query}`;
          fetch(url).then((response) => {
               return response.json();
          }).then((data) => {
               let text = `<div class=list-group-item'>`;
               data.forEach((contact) => {
                    text += `<a href='/user/${contact.cid}/contact' class='list-group-item list-group-item-action'> ${contact.name}</a>`
               });

               text += `</div>`;
               $(".search-result").html(text);
               $(".search-result").show();
          })

     }
}