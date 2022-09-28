/**
	@see jQuery
	@see highlightjs
*/
(function($){
	//External source - https://gist.github.com/ahtcx/0cd94e62691f539160b32ecda18af3d6
    const merge = (target, source) => {
        // Iterate through `source` properties and if an `Object` set property to merge of `target` and `source` properties
        for (let key of Object.keys(source)) {
            if (source[key] instanceof Object) {
                Object.assign(source[key], merge(target[key], source[key]));
            }
        }

        // Join `target` and modified `source`
        Object.assign(target || {}, source);
        return target;
    }

	$.fn.javaEditor = function(options){
		
		const settings = merge({
			code:'',
			execute:true,
			server:"/docker-runner/docker/run/java/",
			version:11,
		}, options || {});
		
		return this.each(function(){
			//add class
			$(this).addClass("java-editor");
			
			//add edit-panel
			const editPanel = $("<div>").addClass("edit-panel");
			$(this).append(editPanel);

			//add editor
			const editor = $("<textarea>").addClass("editor").attr("spellcheck", "false");
			editPanel.append(editor);
			
			//add viewer
			const viewer = $("<div>").addClass("viewer").addClass("language-java");
			editPanel.append(viewer);
			
			let backupKey = null;
			editor.keydown(function(e){
				switch(e.keyCode){
					case 27://ESC
		            case 18://LEFT ALT
		            case 21://RIGHT ALT
		            case 91://LEFT WINDOWS
		            case 92://RIGHT WINDOWS
		            case 93://CONTEXT
		                return false;
				}
				
				switch(e.key){
		            case "Shift":
		            case "Control":
		                backupKey = e.key;
		                return false;
		        }
				
				if(e.key === 'Tab'){
					//backupKey == null ? addIndent(this) : removeIndent(this);
					return false;
				}
			});
			editor.keyup(function(e){
				switch(e.keyCode){
					case 27://ESC
		            case 18://LEFT ALT
		            case 21://RIGHT ALT
		            case 91://LEFT WINDOWS
		            case 92://RIGHT WINDOWS
		            case 93://CONTEXT
		                e.preventDefault();
		                return;
				}
				
				switch(e.key){
		            case "Shift":
		            case "Control":
		                backupKey = null;
		                return false;
		        }
			});
			editor.on("input", function(){
				viewer.text(editor.val());
				//highlight
				hljs.highlightElement(viewer.get(0));
			});
			editor.scroll(function(e){
				viewer.scrollTop($(this).scrollTop());
				viewer.scrollLeft($(this).scrollLeft());
			});
			
			//set code
			editor.val(settings.code);
			viewer.text(settings.code);
			hljs.highlightElement(viewer.get(0));
			
			
			if(settings.execute){
				//result panel
				const executePanel = $("<div>").addClass("result-panel");
				$(this).append(executePanel);
				
				//button panel
				const buttonPanel = $("<div>").addClass("button-panel");
				executePanel.append(buttonPanel);
				
				//execute button
				const button = $("<button>").addClass("execute-btn").text("Execute");
				buttonPanel.append(button);
				
				//terminal
				const terminal = $("<iframe>").addClass("terminal");
				executePanel.append(terminal);

				//button event				
				button.click(function(){
					const value = editor.val();
					if(!value){
						return;
					}
					
					$.ajax({
						url:settings.server+settings.version,
						type:"post",
						data:JSON.stringify({code:value}),
						contentType:"application/json",
						dataType:"text",
						beforeSend:function(){
							button.prop("disabled", true);
						},
						success:function(resp){
							terminal.addClass("active");
							terminal.attr("src", resp);
						},
						complete:function(){
							button.prop("disabled", false);
						},
					});
					
				});
			}
		});
	}
})(jQuery);







