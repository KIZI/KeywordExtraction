<%@ include file="/WEB-INF/jsp/include/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<c:url value="/css/bootstrap.min.css" />" type="text/css" media="all" />
<link rel="stylesheet" href="<c:url value="/css/main.css" />" type="text/css" media="all" />
<script src="<c:url value="/js/bootstrap.min.js" />"></script>
<script src="<c:url value="/js/jquery-1.9.1.min.js" />"></script>
<script src="<c:url value="/js/main.js" />"></script>
<script type="text/javascript">
apiUrl ="<c:url value="/rest/v2" />"; 
</script>

<title>Keyword Extraction</title>
</head>
<body>
    <div class="container">
        <form id="loginForm" class="form-signin">
            <h2 class="form-signin-heading">Please sign in</h2>
            <input id="userName" type="text" class="input-block-level" placeholder="Login" value="test"> 
            <input id="password" type="password" value="heslo"
                class="input-block-level" placeholder="Password"> 
            <button id="loginBtn" class="btn btn-large btn-primary" type="submit">Sign in</button>
            <a href="#" id="addUserLink">Register</a>
        </form>

        <form id="regUser" class="form-signin">
            <h2 class="form-signin-heading">Create an Account</h2>
            <input id="regUserName" type="text" class="input-block-level" placeholder="Login"> 
            <input id="regPassword" type="password" class="input-block-level" placeholder="Password">
            <input id="regPassword2" type="password"  class="input-block-level" placeholder="Retype Password"> 
            <button id="loginBtn" class="btn btn-large btn-primary" type="submit">Register user</button>
            <a href="#" id="loginLink">Sign in</a>
        </form>
        
        <div id="commandPanel" class="container">
            <h1>Keyword extraction</h1>
            <form id="submitFile" class="span6">
                <label for="languages">Select the language</label>
                <select id="languages">
                    <option value="german">German</option>
                    <option value="english">English</option>
                    <option value="dutch">Dutch</option>
                </select>
                <label for="fileName">File name</label>
                <input type="text" id="fileName">
                <label for="fileText">File text</label>
                <textarea id="fileText"></textarea>
                <div>
                <input type="submit" class="btn btn-large btn-primary" value="Submit file" />
                </div>                
            </form>
            <div class="span5">
                <div id="kwList">
                
                </div>
                <div id="fileList">
                </div>
            </div>
        </div>
    </div>
    <div id="formSubmitted"><h3>File was submitted. Indexing...</h3></div>
    <div id="lightbox"></div>
</body>
</html>