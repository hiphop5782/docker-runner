<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Document</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/java-editor.css">
    
    <link rel="stylesheet" href="//unpkg.com/@highlightjs/cdn-assets@11.6.0/styles/github.min.css">
	<script src="//unpkg.com/@highlightjs/cdn-assets@11.6.0/highlight.min.js"></script>

    <script src="https://code.jquery.com/jquery-3.6.0.js"></script>
    <script src="${pageContext.request.contextPath}/js/java-editor.js"></script>
    <script>
        $(function(){
            $("button").click(function(){
                var code = $("textarea").val();
                if(!code) return;
                
                $.ajax({
                    url:"${pageContext.request.contextPath}/docker/run/java/11",
                    type:"post",
                    data:JSON.stringify({code:code}),
                    contentType:"application/json",
                    dataType:"text",
                    success:function(resp){
                        $("iframe").attr("src", resp);
                    },
                });
            });
            
        	$(".code-runner").javaEditor();
        });
    </script>
</head>
<body>
    <textarea rows="15" style="width:100%" class="editor">import java.util.*;
public class Hello {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.print("입력 : ");
        int value = sc.nextInt();
        sc.close();

        System.out.println("입력된 값 : " + value);
    }
}</textarea>
    <button>실행</button>
    <hr>
    <iframe width="100%" height="200"></iframe>
    
    <hr>
    
    <div class="code-runner"></div>
</body>
</html>