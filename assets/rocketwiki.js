/**
 * rocketwiki.js - Javascript library to converts RocketWiki MarkUp language to HTML.
 * You can do whatever with it. Please give me some credits (Apache License v2.0)
 * - Jakob Flierl <jakob.flierl@gmail.com>
 */

var rocketwiki = {}

rocketwiki.process = function(wikitext) {
	var lines = wikitext.split(/\r?\n/);
	
	var html = "";
	
	for (i=0;i<lines.length;i++)
	{
		line = lines[i];
		if (line.match(/^====/)!=null && line.match(/====$/)!=null) {
			html += "<h1>"+line.substring(4,line.length-4)+"</h1>";
		} else if (line.match(/^===/)!=null && line.match(/===$/)!=null) {
                        html += "<h2>"+line.substring(3,line.length-3)+"</h2>";
                } else if (line.match(/^==/)!=null && line.match(/==$/)!=null) {
                        html += "<h3>"+line.substring(2,line.length-2)+"</h3>";
                } else if (line.match(/^=/)!=null && line.match(/=$/)!=null) {
			html += "<h4>"+line.substring(1,line.length-1)+"</h4>";
		} else if (line.match(/^:+/)!=null) {
			// find start line and ending line
			start = i;
			while (i < lines.length && lines[i].match(/^\:+/)!=null) i++;
			i--;
			
			html += rocketwiki.process_indent(lines,start,i);
		} else if (line.match(/^---+(\s*)$/)!=null) {
			html += "<hr/>";
		} else if (line.match(/^(-{1,2}) /)!=null) {
			// find start line and ending line
			start = i;
			while (i < lines.length && lines[i].match(/^(-{1,2}|\#\#+)\:? /)!=null) i++;
			i--;
			
			html += rocketwiki.process_bullet_point(lines,start,i);
		} else if (line.match(/^(\#+) /)!=null) {
			// find start line and ending line
			start = i;
			while (i < lines.length && lines[i].match(/^(\#+|-{2})\:? /)!=null) i++;
			i--;
			
			html += rocketwiki.process_bullet_point(lines,start,i);
		} else {
			html += rocketwiki.process_normal(line);
		}
		
		if (line.length == 0) {
			html += "<br/>\n";
		}
	}
	
	return html;
}

rocketwiki.process_indent = function(lines,start,end) {
	var i = start;
	
	var html = "<dl>";
	
	for(var i=start;i<=end;i++) {
		html += "<dd>";
		
		var this_count = lines[i].match(/^(\:+)/)[1].length;
		
		html += rocketwiki.process_normal(lines[i].substring(this_count));
		
		var nested_end = i;
		for (var j=i+1;j<=end;j++) {
			var nested_count = lines[j].match(/^(\:+)/)[1].length;
			if (nested_count <= this_count) break;
			else nested_end = j;
		}
		
		if (nested_end > i) {
			html += rocketwiki.process_indent(lines,i+1,nested_end);
			i = nested_end;
		}
		
		html += "</dd>";
	}
	
	html += "</dl>";
	return html;
}

rocketwiki.process_bullet_point = function(lines,start,end) {
	var i = start;
	
	var html = (lines[start].charAt(0)=='-')?"<ul>":"<ol>";
	
	for(var i=start;i<=end;i++) {
		html += "<li>";
		var this_count = lines[i].match(/^(-+|\#+) /)[1].length;
		html += rocketwiki.process_normal(lines[i].substring(this_count+1));
		
		// continue previous with #:
		{
			var nested_end = i;
			for (var j = i + 1; j <= end; j++) {
				var nested_count = lines[j].match(/^(-+|\#+)\:? /)[1].length;
				
				if (nested_count < this_count) 
					break;
				else {
					if (lines[j].charAt(nested_count) == ':') {
						html += "<br/>" + rocketwiki.process_normal(lines[j].substring(nested_count + 2));
						nested_end = j;
					} else {
						break;
					}
				}
					
			}
			
			i = nested_end;
		}
		
		// nested bullet point
		{
			var nested_end = i;
			for (var j = i + 1; j <= end; j++) {
				var nested_count = lines[j].match(/^(-+|\#+)\:? /)[1].length;
				if (nested_count <= this_count) 
					break;
				else 
					nested_end = j;
			}
			
			if (nested_end > i) {
				html += rocketwiki.process_bullet_point(lines, i + 1, nested_end);
				i = nested_end;
			}
		}
		
		// continue previous with #:
		{
			var nested_end = i;
			for (var j = i + 1; j <= end; j++) {
				var nested_count = lines[j].match(/^(-+|\#+)\:? /)[1].length;
				
				if (nested_count < this_count) 
					break;
				else {
					if (lines[j].charAt(nested_count) == ':') {
						html += rocketwiki.process_normal(lines[j].substring(nested_count + 2));
						nested_end = j;
					} else {
						break;
					}
				}
					
			}
			
			i = nested_end;
		}
		
		html += "</li>";
	}
	
	html += (lines[start].charAt(0)=='-')?"</ul>":"</ol>";
	return html;
}

rocketwiki.process_url = function(txt) {
	
	var index = txt.indexOf(" ");
	
	if (index == -1) 
	{
		return "<a target='"+txt+"' href='"+txt+"' style='background: url(\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAFZJREFUeF59z4EJADEIQ1F36k7u5E7ZKXeUQPACJ3wK7UNokVxVk9kHnQH7bY9hbDyDhNXgjpRLqFlo4M2GgfyJHhjq8V4agfrgPQX3JtJQGbofmCHgA/nAKks+JAjFAAAAAElFTkSuQmCC\") no-repeat scroll right center transparent;padding-right: 13px;'></a>";
	}
	else
	{
		url = txt.substring(0,index);
		label = txt.substring(index+1);
		return "<a target='"+url+"' href='"+url+"' style='background: url(\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAFZJREFUeF59z4EJADEIQ1F36k7u5E7ZKXeUQPACJ3wK7UNokVxVk9kHnQH7bY9hbDyDhNXgjpRLqFlo4M2GgfyJHhjq8V4agfrgPQX3JtJQGbofmCHgA/nAKks+JAjFAAAAAElFTkSuQmCC\") no-repeat scroll right center transparent;padding-right: 13px;'>"+label+"</a>";
	}
}

rocketwiki.process_image = function(txt) {
	var index = txt.indexOf(" ");
	url = txt;
	label = "";
	
	if (index > -1) {
		url = txt.substring(0,index);
		label = txt.substring(index+1);
	}
	
	return "<img src='"+url+"' alt=\""+label+"\" />";
}

rocketwiki.process_video = function(url) {

	if (url.match(/^(https?:\/\/)?(www.)?youtube.com\//) == null) {
		return "<b>"+url+" is an invalid YouTube URL</b>";
	}
	
	if ((result = url.match(/^(https?:\/\/)?(www.)?youtube.com\/watch\?(.*)v=([^&]+)/)) != null) {
		url = "http://www.youtube.com/embed/"+result[4];
	}
	
	return '<iframe width="480" height="390" src="'+url+'" frameborder="0" allowfullscreen></iframe>';
}

rocketwiki.process_special = function(txt, chIdx, chRegexp, chRep) {
        var index = txt.indexOf(chIdx);
	while(index > -1) {
		txt = txt.replace(chRegexp, chRep);
		index = txt.indexOf(chIdx, index);
	}
	return txt;
}

rocketwiki.process_normal = function(wikitext) {
	
	// Image
	{
		var index = wikitext.indexOf("[http");
		var end_index = wikitext.indexOf("]", index + 1);
		while (index > -1 && end_index > -1) {

			if (wikitext.substring(index+1,end_index).indexOf("png") > -1 ||
			    wikitext.substring(index+1,end_index).indexOf("jpg") > -1 ||
			    wikitext.substring(index+1,end_index).indexOf("jpeg") > -1 ||
			    wikitext.substring(index+1,end_index).indexOf("gif") > -1) {
			
				wikitext = wikitext.substring(0,index) 
							+ rocketwiki.process_image(wikitext.substring(index+1,end_index)) 
							+ wikitext.substring(end_index + 1);
			} else {  // FIXME skip for now
				wikitext = wikitext.substring(0,index) + wikitext.substring(end_index + 1);
			}

			index = wikitext.indexOf("[http");
			end_index = wikitext.indexOf("]", index + 1);
		}
	}
	
	// Video
	{
		var index = wikitext.indexOf("[[Video:");
		var end_index = wikitext.indexOf("]]", index + 8);
		while (index > -1 && end_index > -1) {
			
			wikitext = wikitext.substring(0,index) 
						+ rocketwiki.process_video(wikitext.substring(index+8,end_index)) 
						+ wikitext.substring(end_index+2);
		
			index = wikitext.indexOf("[[Video:");
			end_index = wikitext.indexOf("]]", index + 8);
		}
	}
	
	
	// URL
	var protocols = ["http","ftp","news"];
	
	for (var i=0;i<protocols.length;i++)
	{
		var index = wikitext.indexOf("["+protocols[i]+"://");
		var end_index = wikitext.indexOf("]", index + 1);
		while (index > -1 && end_index > -1) {
		
			wikitext = wikitext.substring(0,index) 
						+ rocketwiki.process_url(wikitext.substring(index+1,end_index)) 
						+ wikitext.substring(end_index+1);
		
			index = wikitext.indexOf("["+protocols[i]+"://",end_index+1);
			end_index = wikitext.indexOf("]", index + 1);
			
		}
	}
	
	var count_b = 0;
	var index = wikitext.indexOf("**");
	while(index > -1) {
		
		if ((count_b%2)==0) wikitext = wikitext.replace(/\*\*/,"<b>");
		else wikitext = wikitext.replace(/\*\*/,"</b>");
		
		count_b++;
		
		index = wikitext.indexOf("**",index);
	}

        var count_u = 0;
        var index = wikitext.indexOf("__");
        while(index > -1) {
                if ((count_u%2)==0) wikitext = wikitext.replace(/__/,"<u>");
                else wikitext = wikitext.replace(/__/,"</u>");
                count_u++;
                index = wikitext.indexOf("__",index);
        }

        var count_big = 0;
        var index = wikitext.indexOf("++");
        while(index > -1) {
                if ((count_big%2)==0) wikitext = wikitext.replace(/\+\+/,"<big>");
                else wikitext = wikitext.replace(/\+\+/,"</big>");
                count_big++;
                index = wikitext.indexOf("++",index);
        }

        var count_small = 0;
        var index = wikitext.indexOf("%%");
        while(index > -1) {
                if ((count_small%2)==0) wikitext = wikitext.replace(/%%/,"<small>");
                else wikitext = wikitext.replace(/%%/,"</small>");
                count_small++;
                index = wikitext.indexOf("%%",index);
        }

        var count_monospace = 0;
        var index = wikitext.indexOf("||");
        while(index > -1) {
                if ((count_monospace%2)==0) wikitext = wikitext.replace(/\|\|/,"<tt>");
                else wikitext = wikitext.replace(/\|\|/,"</tt>");
                count_monospace++;
                index = wikitext.indexOf("||",index);
        }

        var count_nobr = 0;
        var index = wikitext.indexOf("&&");
        while(index > -1) {
                if ((count_nobr%2)==0) wikitext = wikitext.replace(/&&/,"<nobr>");
                else wikitext = wikitext.replace(/&&/,"</nobr>");
                count_nobr++;
                index = wikitext.indexOf("&&",index);
        }

        // TODO stylename, no special interpretation of formatting characters

        var count_quote_single = 0;
        var index = wikitext.indexOf("\"\"\"");
        while(index > -1) {
                if ((count_quote_single%2)==0) wikitext = wikitext.replace(/\"\"\"/,"&#8216;");
                else wikitext = wikitext.replace(/\"\"\"/,"&#8217;");
                count_quote_single++;
                index = wikitext.indexOf("\"\"\"",index);
        }

        var count_quote_normal = 0;
        var index = wikitext.indexOf("\"\"");
        while(index > -1) {
                if ((count_quote_normal%2)==0) wikitext = wikitext.replace(/\"\"/,"&#8220;");
                else wikitext = wikitext.replace(/\"\"/,"&#8221;");
                count_quote_normal++;
                index = wikitext.indexOf("\"\"",index);
        }

        var count_quote_ger_single = 0;
        var index = wikitext.indexOf(",,,");
        while(index > -1) {
                if ((count_quote_ger_single%2)==0) wikitext = wikitext.replace(/,,,/,"&#132;");
                else wikitext = wikitext.replace(/,,,/,"&#148;");
                count_quote_ger_single++;
                index = wikitext.indexOf(",,,",index);
        }

        var count_quote_ger_double = 0;
        var index = wikitext.indexOf(",,");
        while(index > -1) {
                if ((count_quote_ger_double%2)==0) wikitext = wikitext.replace(/,,/,"&#132;");
                else wikitext = wikitext.replace(/,,/,"&#148;");
                count_quote_ger_double++;
                index = wikitext.indexOf(",,",index);
        }

        var count_quote_fr_single = 0;
        var index = wikitext.indexOf("<<<");
        while(index > -1) {
                if ((count_quote_fr_single%2)==0) wikitext = wikitext.replace(/<<</,"&lsaquo;");
                else wikitext = wikitext.replace(/>>>/,"&rsaquo;");
                count_quote_fr_single++;
                index = wikitext.indexOf(">>>",index);
        }

        var count_quote_fr = 0;
        var index = wikitext.indexOf("<<");
        while(index > -1) {
                if ((count_quote_fr%2)==0) wikitext = wikitext.replace(/<</,"&#171;");
                else wikitext = wikitext.replace(/>>/,"&#187;");
                count_quote_fr++;
                index = wikitext.indexOf(">>",index);
        }

        var index = wikitext.indexOf("(_)");
        while(index > -1) {
                wikitext = wikitext.replace(/\(_\)/,"&nbsp;");
                index = wikitext.indexOf("(_)",index);
        }

	// FIXME soft hyphen

	wikitext = rocketwiki.process_special(wikitext, "{EUR}", /\{EUR\}/, "&#8364;");
	wikitext = rocketwiki.process_special(wikitext, "{Ae}", /\{Ae\}/, "&Auml;");
	wikitext = rocketwiki.process_special(wikitext, "{ae}", /\{ae\}/, "&auml;");
	wikitext = rocketwiki.process_special(wikitext, "{Oe}", /\{Oe\}/, "&Ouml;");
	wikitext = rocketwiki.process_special(wikitext, "{oe}", /\{oe\}/, "&ouml;");
	wikitext = rocketwiki.process_special(wikitext, "{Ue}", /\{Ue\}/, "&Uuml;");
	wikitext = rocketwiki.process_special(wikitext, "{ue}", /\{ue\}/, "&uuml;");
	wikitext = rocketwiki.process_special(wikitext, "{sz}", /\{sz\}/, "&#223;");
	wikitext = rocketwiki.process_special(wikitext, "{ss}", /\{ss\}/, "&#7838;");

	wikitext = rocketwiki.process_special(wikitext, "{.}", /\{\.\}/, "&middot;");

        var index = wikitext.indexOf("{...}"); while(index > -1) { wikitext = wikitext.replace(/\{\...\}/,"&hellip;"); index = wikitext.indexOf("{...}",index); }

	// FIXME more arrows and special chars

        var index = wikitext.indexOf("{<->}"); while(index > -1) { wikitext = wikitext.replace(/\{\<->\}/,"&harr;"); index = wikitext.indexOf("{<->}",index); }
        var index = wikitext.indexOf("{->}"); while(index > -1) { wikitext = wikitext.replace(/\{\->\}/,"&rarr;"); index = wikitext.indexOf("{->}",index); }
        var index = wikitext.indexOf("{=>}"); while(index > -1) { wikitext = wikitext.replace(/\{\=>\}/,"&rArr;"); index = wikitext.indexOf("{=>}",index); }

        var index = wikitext.indexOf("{C}"); while(index > -1) { wikitext = wikitext.replace(/\{\C\}/,"&copy;"); index = wikitext.indexOf("{C}",index); }
        var index = wikitext.indexOf("{R}"); while(index > -1) { wikitext = wikitext.replace(/\{\R\}/,"&reg;"); index = wikitext.indexOf("{R}",index); }
        var index = wikitext.indexOf("{TM}"); while(index > -1) { wikitext = wikitext.replace(/\{\TM\}/,"&trade;"); index = wikitext.indexOf("{TM}",index); }

        var index = wikitext.indexOf("{deg}"); while(index > -1) { wikitext = wikitext.replace(/\{deg\}/,"&deg;"); index = wikitext.indexOf("{deg}",index); }

	wikitext = rocketwiki.process_special(wikitext, "{---}", /\{---\}/, "&mdash;");
	wikitext = rocketwiki.process_special(wikitext, "{--}", /\{--\}/, "&ndash;");

	wikitext = wikitext.replace(/<\/b><\/i>/g,"</i></b>");

	return wikitext;
}

