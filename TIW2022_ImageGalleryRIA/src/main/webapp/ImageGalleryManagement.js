{
    let album_details, myalbums_list, otheralbums_list, pageOrchestrator = new PageOrchestrator();
    let formManager;
    let selected_photo_id = -1;

    window.addEventListener("load", () => {
        if (sessionStorage.getItem("username") == null) {
            window.location.href = "index.html"; //redirect
        } else {
            pageOrchestrator.start(); // initialize the components
            pageOrchestrator.refresh();// display initial content
        }
    }, false);

    function AlbumList(alert_container, albumlist_body){ //both used for "my albums" and "other users' albums"
        this.alert_container = alert_container;
        this.albumlist_body = albumlist_body;
        this.row = null;

        //asks the server for the albums list
        this.reset = function(query_parameters){
            var self = this;
            /*
            if the query parameters specifies "other_albums" we will use this list to represent the "other users' album"
            otherwise this will be used to represent "my albums". Slightly differences applies (such as the draggable rows).
             */
            if(query_parameters !== "other_albums") query_parameters = "";
            makeCall("GET", "GetAlbums?" + query_parameters, null, null, function(req){
                if (req.readyState == 4) {
                    var message = req.responseText;
                    if (req.status == 200) {
                        var albumsToShow = JSON.parse(message);
                        self.update(albumsToShow); // self visible by closure
                    } else if (req.status == 403) {
                        window.location.href = req.getResponseHeader("Location");
                        window.sessionStorage.removeItem('username');
                    }
                    else {
                        self.alert_container.textContent = "Errore non specificato";
                        self.alert_container.setAttribute("hidden", false);
                        setTimeout(()=>{self.alert_container.setAttribute("hidden", true)}, 10000);
                    }
                }
            });
        }

        //displays the album list
        this.update = function(albumlist){
            this.albumlist_body.innerHTML = ""; //empties the container body
            var self = this;
            if(albumlist.length === 0){ //no albums to show
                let row = document.createElement("tr");
                let col = document.createElement("td");
                let colspan = albumlist_body.id === "myalbums_body" ? 3 : 4;
                col.setAttribute("colspan", colspan+"");
                col.textContent = "No albums";
                row.appendChild(col);
                this.albumlist_body.appendChild(row);
            }
            albumlist.forEach(function(album){ //creates and displays a row for each album
                let row = document.createElement("tr");
                if(albumlist_body.id === "myalbums_body"){ //"my albums" need some special treatments (draggable rows)
                    row.draggable = true;
                    row.setAttribute("album_id", album.id);
                    row.setAttribute("order_id", album.sort_number);
                    row.addEventListener('dragstart', (event) => { //drag start
                        this.row = event.target;
                    });
                    row.addEventListener('dragover', (event) => { //drag over
                        event.preventDefault();
                        let rows = Array.from(event.target.parentNode.parentNode.children);
                        let new_index = rows.indexOf(event.target.parentNode)
                        let old_index = rows.indexOf(this.row)
                        if(old_index === -1) return;
                        if(new_index > old_index)
                            event.target.parentNode.after(this.row);
                        else
                            event.target.parentNode.before(this.row);
                    });
                    row.addEventListener('drop', (event) => { //final dragging event
                        document.getElementById("update-order-button").removeAttribute("hidden");
                    });
                }
                let id_cell = document.createElement("td");
                let anchor = document.createElement("a");
                let title_cell = document.createElement("td");
                title_cell.textContent = album.title;
                row.appendChild(title_cell);
                let date_cell = document.createElement("td");
                date_cell.textContent = album.creation_date;
                row.appendChild(date_cell);
                if(albumlist_body.id !== "myalbums_body"){ //if it's not mine, the author has to be displayed
                    let author_cell = document.createElement("td");
                    author_cell.textContent = album.username;
                    row.appendChild(author_cell);
                }
                id_cell.appendChild(anchor);
                id_cell.classList.add("clickable-show-album");
                anchor.innerText = "Show";
                anchor.setAttribute("album_id", album.id);
                anchor.addEventListener("click", (e) => {
                    album_details.reset(e.target.getAttribute("album_id"), 0);
                }, false);
                row.appendChild(id_cell);
                albumlist_body.appendChild(row);
            });
        }
    }

    function AlbumDetails(
        alert_container,
        fivethumbnails_container,
        albumtitle_container,
        albumdate_container,
        modalwindow_button,
        phototitle_container,
        photouploaddate_container,
        photodescription_container,
        photocomments_container,
        photo_container,
        album_container,
        addphotoform_container
    ){
        this.alert_container = alert_container;
        this.fivethumbnails_container = fivethumbnails_container;
        this.albumtitle_container = albumtitle_container;
        this.albumdate_container = albumdate_container;
        this.modalwindow_button = modalwindow_button;
        this.phototitle_container = phototitle_container;
        this.photouploaddate_container = photouploaddate_container;
        this.photodescription_container = photodescription_container;
        this.photocomments_container = photocomments_container;
        this.photo_container = photo_container;
        this.album_container = album_container;
        this.addphotoform_container = addphotoform_container;
        this.last_photo_id = -1;
        this.last_album_id = -1;
        this.thumbnails = [];

        //asks the server for a particular album details
        this.reset = function(album_id){
            if(album_id !== -1) { //if album_id is -1 we don't have to update any view (no album has been selected)
                this.album_container.removeAttribute("hidden");
                this.addphotoform_container.setAttribute("hidden", true);
                this.albumtitle_container.textContent = "";
                this.albumdate_container.textContent = "";
                this.last_album_id = album_id;
                this.album_container.scrollIntoView();
                this.thumbnails = [];
                var self = this;
                makeCall("GET", "GetAlbumDetails?album_id=" + album_id, null, null, function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var albumToShow = JSON.parse(message);
                            //preload the photos
                            for (let i = 0; i < albumToShow.photos.length; i++) {
                                let img = new Image();
                                img.src = "DownloadPhoto?imageId=" + (albumToShow.photos[i].path).replace("\\", "/");
                                img.id = albumToShow.photos[i].id;
                                img.title = albumToShow.photos[i].title;
                                self.thumbnails.push(img);
                            }
                            self.update(albumToShow, 0); // self visible by closure
                        } else if (req.status == 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem('username');
                        } else {
                            self.alert_container.removeAttribute("hidden");
                            self.alert_container.textContent = message;
                            window.scrollTo({top: 0, behavior: 'smooth'});
                            setTimeout(() => {
                                self.alert_container.setAttribute("hidden", true)
                            }, 10000);
                        }
                    }
                });
            }
        }

        //displays the album details
        this.update = function(album, start_index){
            this.fivethumbnails_container.innerHTML = "";
            var self = this;
            let available_photos = album.available_photos;
            if(available_photos.length > 0) //if the user can upload at least 1 photo, the form becomes available
                this.addphotoform_container.removeAttribute("hidden");
            this.addAvailablePhotos(available_photos);
            let HasPrevious = start_index > 0;
            let HasNext = start_index + 5 < this.thumbnails.length;
            this.albumtitle_container.textContent = album.album.title;
            this.albumdate_container.textContent = album.album.creation_date;

            //previous arrow icon
            let previous = document.createElement("a");
            let prev_container = document.createElement("div");
            prev_container.classList.add("col-md-1");
            let prev_icon = document.createElement("i");
            prev_icon.classList.add("fas", "fa-angle-left", "fa-3x");
            previous.appendChild(prev_icon);
            prev_container.appendChild(previous);
            this.fivethumbnails_container.appendChild(prev_container);
            //if it's available we add the event listener
            if(HasPrevious){
                previous.href = "#album-title-container";
                let prev_start = start_index - 5;
                previous.addEventListener('click', (e) => {
                    album_details.update(album, prev_start);
                })
            } else {
                prev_icon.classList.add("fa-disabled");
            }

            //5 thumbnails
            let thumbnails_container = document.createElement("div");
            thumbnails_container.classList.add("col-md", "text-center");
            if(this.thumbnails.length === 0){
                thumbnails_container.innerText = "Empty album";
            } else {
                let thumbnails_row = document.createElement("div");
                thumbnails_row.classList.add("row");
                thumbnails_container.appendChild(thumbnails_row);
                for(let i = start_index; i < Math.min(start_index + 5, this.thumbnails.length); i++){
                    let pic_container = document.createElement("div");
                    pic_container.classList.add("col");
                    let pic_title = document.createElement("div");
                    let pic = document.createElement("img");
                    pic.classList.add("img", "rounded-7", "maxwidth-thumbnail", "with-lateral-margin");
                    pic.src = this.thumbnails[i].src;
                    pic.height = 100;
                    pic.photoid = this.thumbnails[i].id;
                    pic_title.textContent = this.thumbnails[i].title;
                    pic_container.appendChild(pic);
                    pic_container.appendChild(pic_title);
                    thumbnails_row.appendChild(pic_container);
                    //event listener to show the modal window
                    pic.addEventListener('mouseenter', (e)=>{
                        this.requestPhoto(pic.photoid, "show photo");
                    });
                }
            }
            this.fivethumbnails_container.appendChild(thumbnails_container);

            //next arrow icon
            let next = document.createElement("a");
            let next_container = document.createElement("div");
            next_container.classList.add("col-md-1");
            let next_icon = document.createElement("i");
            next_icon.classList.add("fas", "fa-angle-right", "fa-3x");
            next.appendChild(next_icon);
            next_container.appendChild(next);
            this.fivethumbnails_container.appendChild(next_container);
            //if next is available we add its event listener
            if(HasNext){
                next.href = "#album-title-container";
                let next_start = start_index + 5;
                next.addEventListener('click', (e) => {
                    album_details.update(album, next_start);
                })
            } else {
                next_icon.classList.add("fa-disabled");
            }
        }

        //asks the server for a photo details
        this.requestPhoto = function(photo_id, view_action){
            var self = this;
            this.last_photo_id = photo_id;
            makeCall("GET", "GetPhoto?photo_id=" + photo_id, null, null, function(req){
                if (req.readyState == 4) {
                    var message = req.responseText;
                    if (req.status == 200) {
                        var photoToShow = JSON.parse(req.responseText);
                        if(view_action === "show photo") {
                            self.showPhoto(photoToShow);
                        } else {
                            self.showComments(photoToShow);
                        }
                    } else if (req.status == 403) {
                        window.location.href = req.getResponseHeader("Location");
                        window.sessionStorage.removeItem('username');
                    }
                    else {
                        self.alert_container.removeAttribute("hidden");
                        self.alert_container.textContent = message;
                        setTimeout(()=>{self.alert_container.setAttribute("hidden", true)}, 10000);
                    }
                }
            });
        }

        //prepares the selected photo view and launches the modal window
        this.showPhoto = function(photo_packet){
            let photo = photo_packet.photo;
            this.phototitle_container.textContent = photo.title;
            this.photodescription_container.textContent = photo.alt_text;
            this.photouploaddate_container.textContent =  photo.upload_date;
            this.photo_container.src = "DownloadPhoto?imageId=" + (photo.path).replace("\\", "/");
            selected_photo_id = photo.id;
            this.showComments(photo_packet);
            this.modalwindow_button.click();
        }

        //displays the comments
        this.showComments = function(photo_packet){
            let comments = photo_packet.comments;
            if(comments.length === 0){
                this.photocomments_container.textContent = "No comments yet";
            } else {
                this.photocomments_container.innerHTML = "";
            }
            for(let i = 0; i < comments.length; i++){
                let comment_container = document.createElement("div");
                comment_container.classList.add("d-flex", "flex-start");
                let avatar_container = document.createElement("div");
                avatar_container.classList.add("rounded-circle", "shadow-1-strong", "me-3", "d-flex", "justify-content-center", "align-items-center", "avatar-preview", "h1", "bg-danger", "text-white");
                avatar_container.innerText = comments[i].username.substring(0,1).toUpperCase();
                comment_container.appendChild(avatar_container);
                let commentbody_container = document.createElement("div");
                let user_container = document.createElement("h6");
                user_container.classList.add("fw-bold", "mb-1");
                user_container.textContent = comments[i].username;
                commentbody_container.appendChild(user_container);
                let tstamp_container = document.createElement("div");
                tstamp_container.classList.add("d-flex", "align-items-center", "mb-0");
                let tstamptext_container = document.createElement("p");
                tstamptext_container.classList.add("mb-0");
                tstamptext_container.textContent = comments[i].timestamp;
                tstamp_container.appendChild(tstamptext_container);
                commentbody_container.appendChild(tstamp_container);
                let text_container = document.createElement("p");
                text_container.classList.add("mb-4");
                text_container.textContent = comments[i].text;
                commentbody_container.appendChild(text_container);
                comment_container.appendChild(commentbody_container);
                this.photocomments_container.appendChild(comment_container);
            }
        }

        //add an available-to-upload photo to the "add to album" form
        this.addAvailablePhotos = function(photos){
            let addphoto_form = document.getElementById("add-photo-in-album");
            addphoto_form.innerHTML = "";
            for(let i = 0; i < photos.length; i++){
                let option = document.createElement("option");
                option.value = photos[i].id;
                option.text = photos[i].id + " - " + photos[i].title;
                addphoto_form.appendChild(option);
            }
        }

        //refresh the comments view
        this.refreshComments = function(){
            this.requestPhoto(this.last_photo_id, "show comments");
        }

        //refresh the available-to-upload form view
        this.refreshAvailablePhotos = function(){
            this.reset(this.last_album_id);
        }

        this.getAlbumID = function(){
            return this.last_album_id;
        }
    }

    //the form manager attaches the corresponding event listener to each form and manages the submit event
    function FormManager(
        alert_container,
        addcomment_form,
        createalbum_form,
        addtoalbum_form,
        uploadphoto_form,
        updateorder_form
    ){
        this.alert_container = alert_container;
        this.addcomment_form = addcomment_form;
        this.createalbum_form = createalbum_form;
        this.addtoalbum_form = addtoalbum_form;
        this.uploadphoto_form = uploadphoto_form;
        this.updateorder_form = updateorder_form;

        //attaches each listener to its form
        this.start = function(){
            this.attachAddComment();
            this.attachCreateAlbum();
            this.attachAddToAlbum();
            this.attachUploadPhoto();
            this.attachUpdateOrder();
        }

        //when creating the event listener we also specify how to obtain data from the form
        this.attachAddComment = function(){
            this.addcomment_form.addEventListener('click', (e) => {
                e.stopImmediatePropagation();
                e.preventDefault();
                var form = e.target.closest("form");
                var formData = new FormData(); //in this case we create manually the formdata and we send it to the server
                //text, idphoto
                formData.append(
                    "comment", document.getElementById("add-comment-text").value
                )
                formData.append(
                    "photo-id", selected_photo_id
                )
                this.sendForm("POST", form, formData, "AddComment", pageOrchestrator.refreshComments);
            });
        }

        this.attachCreateAlbum = function(){
            this.createalbum_form.addEventListener('click', (e) => {
                e.stopImmediatePropagation();
                e.preventDefault();
                var form = e.target.closest("form");
                var formData = new FormData();
                formData.append(
                    "title", document.getElementById("ca-title").value
                )
                this.sendForm("POST", form, formData, "CreateAlbum", pageOrchestrator.refreshMyAlbums);
                window.scrollTo({ top: 0, behavior: 'smooth' });
            });
        }

        this.attachAddToAlbum = function(){
            this.addtoalbum_form.addEventListener('click', (e) => {
                e.stopImmediatePropagation();
                e.preventDefault();
                var form = e.target.closest("form");
                var formData = new FormData();
                //photo-id, album-id
                formData.append(
                    "photo-id", document.getElementById("add-photo-in-album").value
                )
                formData.append(
                    "album-id", album_details.getAlbumID()
                )
                this.sendForm("POST", form, formData, "AddToAlbum", pageOrchestrator.refreshAvailablePhotos);
            });
        }

        this.attachUploadPhoto = function(){
            this.uploadphoto_form.addEventListener('click', (e) => {
                e.stopImmediatePropagation();
                e.preventDefault();
                var form = e.target.closest("form");
                var formData = new FormData();
                formData.append(
                    "title", document.getElementById("up-title").value
                )
                formData.append(
                    "alt", document.getElementById("up-alt").value
                )
                formData.append(
                    "file", document.getElementById("up-file").files[0]
                )
                this.sendForm("POST", form, formData, "UploadPhoto", pageOrchestrator.refreshAvailablePhotos);
            });
        }

        this.attachUpdateOrder = function(){
            this.updateorder_form.addEventListener('click', (e) => {
                e.stopImmediatePropagation();
                e.preventDefault();
                var form = e.target.closest("form");
                var formData = new FormData();
                //getting the albums order
                let albums = [];
                let rows = document.getElementById("myalbums_body").rows;
                for(let i = 0; i < rows.length; i++){
                    albums.push(rows[i].getAttribute("album_id"));
                }
                //only one json parameter, which will be parsed as a list
                formData.append("albums_order", JSON.stringify(albums));
                document.getElementById("update-order-button").hidden = true;
                this.sendForm("POST", null, formData, "UpdateOrder", null);
            });
        }

        //sends the form's data to the server
        this.sendForm = function(method, form, formData, url, callback){
            var self = this;
            if (form == null || form.checkValidity()) { //form null se non è previsto un form contenitore
                makeCall(
                    method,
                    url,
                    formData,
                    form,
                    function(x) {
                        if (x.readyState == XMLHttpRequest.DONE) {
                            var message = x.responseText;
                            switch (x.status) {
                                case 200:
                                    if(callback !== null){
                                        callback();
                                    }
                                    break;
                                case 400: // bad request
                                case 401: // unauthorized
                                case 500: // server error
                                    self.alert_container.removeAttribute("hidden");
                                    self.alert_container.textContent = message;
                                    window.scrollTo({ top: 0, behavior: 'smooth' });
                                    setTimeout(()=>{self.alert_container.setAttribute("hidden", true)}, 10000);
                                    break;
                            }
                        }
                    },
                    true
                );
            } else {
                form.reportValidity();
            }
        }
    }

    function PageOrchestrator(){
        var alert_container = document.getElementById("id_alert");
        this.start = function(){
            myalbums_list = new AlbumList(
                alert_container,
                document.getElementById("myalbums_body")
            );
            otheralbums_list = new AlbumList(
                alert_container,
                document.getElementById("otheralbums_body")
            );
            album_details = new AlbumDetails(
                alert_container,
                document.getElementById("five-thumbnails-container"),
                document.getElementById("album-title-container"),
                document.getElementById("album-date-container"),
                document.getElementById("launch-modal-button"),
                document.getElementById("photo-title"),
                document.getElementById("photo-upload-date"),
                document.getElementById("photo-description"),
                document.getElementById("photo-comments-container"),
                document.getElementById("photo-hires"),
                document.getElementById("selected_album"),
                document.getElementById("add-photo-in-album-container")
            );
            formManager = new FormManager(
                alert_container,
                document.getElementById("add-comment-button"),
                document.getElementById("create-album-button"),
                document.getElementById("add-photo-in-album-button"),
                document.getElementById("upload-photo-button"),
                document.getElementById("update-order-button")
            )
            formManager.start();
            this.refresh();
        }

        this.refresh = function(){
            myalbums_list.reset("my_albums");
            otheralbums_list.reset("other_albums");
        }

        this.refreshMyAlbums = function(){
            myalbums_list.reset("my_albums");
        }

        this.refreshComments = function(){
            album_details.refreshComments();
        }

        this.refreshAvailablePhotos = function(){
            album_details.refreshAvailablePhotos();
        }
    }

}