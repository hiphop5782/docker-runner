<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Java Runner</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/java-editor.css">
    
    <link rel="stylesheet" href="//unpkg.com/@highlightjs/cdn-assets@11.6.0/styles/github.min.css">
	<script src="//unpkg.com/@highlightjs/cdn-assets@11.6.0/highlight.min.js"></script>

    <script src="https://code.jquery.com/jquery-3.6.0.js"></script>
    <script src="${pageContext.request.contextPath}/js/java-editor.js"></script>
    <script>
        $(function(){
        	let code = "";
        	code += "import java.lang.*;\r\n";
        	code += "public class Hello {\r\n";
        	code += "\tpublic static void main(String[] args){\r\n";
        	code += "\t\t//insert code\r\n";
        	code += "\t\tfor(int i=1; i<=5; i++){\r\n";
        	code += "\t\t\tSystem.out.println(\"Hello World \" + i);\r\n";
        	code += "\t\t}\r\n";
        	code += "\t}\r\n";
        	code += "}\r\n";
        	
        	$(".code-runner").javaEditor({
        		code:code,
        	});
        });
    </script>
</head>
<body>
    <div class="code-runner"></div>
</body>
</html>