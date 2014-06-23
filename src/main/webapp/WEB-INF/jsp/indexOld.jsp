<%@ include file="/WEB-INF/jsp/include/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<c:url value="/css/main.css" />" type="text/css" media="all" />
<title>Keyword Extraction</title>
</head>
<body>
    <h1>Keyword extraction</h1>
    <div id="german">
        <c:if test="${not empty deKeywords}">
            <ul class="keywords">
                <c:forEach var="deKeyword" items="${deKeywords}">
                    <li><c:out value="${deKeyword.word}" /></li>
                </c:forEach>
            </ul>
        </c:if>
        <form method="post" action="<c:url value="/app/german/index/" />" enctype="multipart/form-data">
            <fieldset>
                <legend>German Keyword Extraction</legend>
                <div>
                    <label for="defilename">File Name</label> <input id="defilename" type="text" name="filename"
                        value="<c:out value="${deFileName}" />" />
                </div>
                <div>
                    <label for="defiletext">Text</label>
                    <textarea id="defiletext" name="filetext"><c:out value="${deKvText}" /></textarea>
                </div>
                <input type="submit" value="Submit German File" />
            </fieldset>
        </form>
        <c:if test="${not empty deFiles}">
            <ul class="files">
                <c:forEach var="deFile" items="${deFiles}">
                    <li><a href="?defile=<c:out value="${deFile.fileId}" />"><c:out value="${deFile.name}" /></a> <a href="<c:url value="/app/german/delete/"><c:param name="fileId" value="${deFile.fileId}" /></c:url>">[X]</a></li>
                </c:forEach>
            </ul>
        </c:if>

    </div>

    <div id="dutch">
        <c:if test="${not empty nlKeywords}">
            <ul class="keywords">
                <c:forEach var="nlKeyword" items="${nlKeywords}">
                    <li><c:out value="${nlKeyword.word}" /></li>
                </c:forEach>
            </ul>
        </c:if>
        <form method="post" action="<c:url value="/app/dutch/index/" />" enctype="multipart/form-data">
            <fieldset>
                <legend>Dutch Keyword Extraction</legend>
                <div>
                    <label for="nlfilename">File Name</label> <input id="nlfilename" type="text" name="filename"
                        value="<c:out value="${nlFileName}" />" />
                </div>
                <div>
                    <label for="nlfiletext">Text</label>
                    <textarea id="nlfiletext" name="filetext"><c:out value="${nlKvText}" /></textarea>
                </div>
                <input type="submit" value="Submit Dutch File" />
            </fieldset>
        </form>
        <c:if test="${not empty nlFiles}">
            <ul class="files">
                <c:forEach var="nlFile" items="${nlFiles}">
                    <li><a href="?nlfile=<c:out value="${nlFile.fileId}" />"><c:out value="${nlFile.name}" /></a>  <a href="<c:url value="/app/dutch/delete/"><c:param name="fileId" value="${nlFile.fileId}" /></c:url>">[X]</a></li>
                </c:forEach>
            </ul>
        </c:if>
    </div>
</body>
</html>