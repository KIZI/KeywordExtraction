var key;
var apiUrl;

var languageMap = {
    1: "english",
    2: "german",
    3: "dutch"
}

function deleteFile(fileId, language) {
    $.ajax({
        url: apiUrl + "/" + language + "/" + fileId,
        type: "DELETE",
        headers: {"userKey": key},
        success: function(data) {
            alert("File was deleted");
            readFiles();
        },
        error: function (xhr, ajaxOptions, thrownError) {
            alert(xhr.status + " " + thrownError + " " + xhr.responseText);
            $("#commandPanel").hide();
            $("#loginForm").show();
        }
     }); 
}

function readFile(fileId, fileName, languageId) {
    $.ajax({
        url: apiUrl + "/" + fileId,
        type: "GET",
        headers: {"userKey": key},
        success: function(data) {
            var kwHtml = "";
            for (index in data.keywords) {
                kwHtml += "<div>" + data.keywords[index].word + " ... " + data.keywords[index].confidence + "</div>";
            }
            $("#kwList").html(kwHtml);
            $("#languages").val(languageId);
            $("#fileText").val(data.originalText);
            $("#fileName").val(fileName);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            alert(xhr.status + " " + thrownError + " " + xhr.responseText);
            $("#commandPanel").hide();
            $("#loginForm").show();
        }
     }); 
}

function readFiles() {
    $.ajax({
        url: apiUrl,
        type: "GET",
        headers: {"userKey": key},
        success: function(data) {
            var filesHtml = "";
            for (index in data) {
                filesHtml += "<div><a class=\"fileLink\" id=\"" + data[index].fileId + "\" href=\"" + data[index].languageId +"\">" + data[index].name + "</a> <a class=\"delFile\" id=\"" + data[index].fileId + "\" href=\"" + data[index].languageId +"\" title=\"Delete file\">[X]</a></div>";
            }
            $("#fileList").html(filesHtml);
            $(".fileLink").click(function(event) {
                event.preventDefault();
                readFile($(this).attr("id"), $(this).text(), languageMap[$(this).attr("href")]);
            });
            $(".delFile").click(function(event) {
                event.preventDefault();
                deleteFile($(this).attr("id"), languageMap[$(this).attr("href")]);
            });
        },
        error: function (xhr, ajaxOptions, thrownError) {
            alert(xhr.status + " " + thrownError + " " + xhr.responseText);
            $("#commandPanel").hide();
            $("#loginForm").show();
        }
     }); 
}

$(document).ready(function(){
    $("#addUserLink").click(function(){
        $("#loginForm").hide();
        $("#regUser").show();
    });
    
    $("#loginLink").click(function(){
        $("#regUser").hide();
        $("#loginForm").show();
    });  
    
    $("#regUser").submit(function(event){
        event.preventDefault();
        if (!$("#regUserName").val() || !$("#regPassword").val()) {
            alert("All information is required");
            return false;
        }
        if ($("#regPassword").val() != $("#regPassword2").val()) {
            alert("Passwords do not match");
            return false;
        }
        
        $.ajax({
            url: apiUrl + "/user",
            type: "POST",
            data: {"userName": $("#regUserName").val(), "password": $("#regPassword").val()},
            success: function(data) { 
                key = data;
                $("#loginForm").hide();
                $("#regUser").hide();
                $("#commandPanel").show();
                readFiles();
            },
            error: function (xhr, ajaxOptions, thrownError) {
                alert(xhr.status + " " + thrownError + " " + xhr.responseText);
            }
         });
    });    
    
    $("#loginForm").submit(function(event){
        event.preventDefault();
        $.ajax({
            url: apiUrl + "/user",
            type: "GET",
            headers: {"userName": $("#userName").val(), "password": $("#password").val()},
            success: function(data) { 
                key = data;
                $("#loginForm").hide();
                $("#commandPanel").show();
                readFiles();
            },
            error: function (xhr, ajaxOptions, thrownError) {
                alert(xhr.status + " " + thrownError + " " + xhr.responseText);
            }
         });
    });
    
    $("#submitFile").submit(function(event) {
        event.preventDefault();
        $("#lightbox").show();
        $("#formSubmitted").show();
        $.ajax({
            url: apiUrl + "/" + $("#languages").val(),
            type: "POST",
            headers: {"userKey": key},
            data: {
                fileName: $("#fileName").val(),
                fileText: $("#fileText").val()
            },
            success: function(data) {
                $("#lightbox").hide();
                $("#formSubmitted").hide();
                readFiles();
                readFile(data.fileId, $("#fileName").val(), $("#languages").val());
            },
            error: function (xhr, ajaxOptions, thrownError) {
                alert(xhr.status + " " + thrownError + " " + xhr.responseText);
                $("#commandPanel").hide();
                $("#loginForm").show();
            }
         });       
    });
});