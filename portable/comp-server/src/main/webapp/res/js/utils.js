/* ================== INTERNET ================== */

/* Данная функция создаёт кроссбраузерный объект XMLHTTP */
/*function getXmlHttp_2() {
	var xmlhttp;
	try {
		xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
	} catch (e) {
		try {
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (E) {
			xmlhttp = false;
		}
	}
	if (!xmlhttp && typeof XMLHttpRequest!='undefined') {
		xmlhttp = new XMLHttpRequest();
	}
	return xmlhttp;
}*/

/* Данная функция создаёт кроссбраузерный объект XMLHTTP */
function getXmlHttp()
{
	if (window.XMLHttpRequest)
	{
		// code for IE7+, Firefox, Chrome, Opera, Safari
		return new XMLHttpRequest();
	}
	else
	{
		// code for IE6, IE5
		return new ActiveXObject("Microsoft.XMLHTTP");
	}
}

function doGet(url, async, onSuccess, onFailure)
{
	var xmlhttp = getXmlHttp();
	xmlhttp.open("GET", url, async);
	xmlhttp.onreadystatechange = function()
	{
		if (xmlhttp.readyState == 4)
		{
			if(xmlhttp.status == 200)
			{
				onSuccess(xmlhttp.responseText);
			}
			else
			{
				onFailure();
			}
		}
	};
	xmlhttp.send();
}

function doPost(url, request, async, onSuccess, onFailure)
{
	var xmlhttp = getXmlHttp();
	xmlhttp.open('POST', url, async);
	xmlhttp.onreadystatechange = function()
	{
		if (xmlhttp.readyState == 4)
		{
			if(xmlhttp.status == 200)
			{
				onSuccess(xmlhttp.responseText);
			}
			else
			{
				onFailure();
			}
		}
	};
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request);
}

function doPut(url, request, async, onSuccess, onFailure)
{
	var xmlhttp = getXmlHttp();
	xmlhttp.open('PUT', url, async);
	xmlhttp.onreadystatechange = function()
	{
		if (xmlhttp.readyState == 4)
		{
			if(xmlhttp.status == 200)
			{
				onSuccess(xmlhttp.responseText);
			}
			else
			{
				onFailure();
			}
		}
	};
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request);
}

/* ================== DIALOGS ================== */

function inputFloat(msg, oldValue)
{
	// both positive and negative

	var newValue = prompt(msg, oldValue);
	if ((newValue != null) && (newValue != ""))
	{
		var t = newValue.replace(',', '.');
		return parseFloat(t);
	}
	else
	{
		return -1;
	}
}

function inputTime(msg, oldTime)
{
	// 09:30	YES
	// 9:30		NO (yet)
	// 9.30		NO (yet)

	var newValue = prompt(msg, formatTime(oldTime));

	if ((newValue != null) && (newValue != ""))
	{
		var hour = parseFloat(newValue.substr(0, 2));
		var min = parseFloat(newValue.substr(3, 5));
		
		var date = new Date();
		date.setHours(hour, min, 0, 0);
		return date;
	}
	else
	{
		return null;
	}
}

function inputText(msg)
{
	return prompt(msg, "");
}

/* ================== FORMATTING ================== */

function valid(date)
{
	// yyyy-mm-dd

	if (!date.match(/^[0-9]{4}\-(0[1-9]|1[012])\-(0[1-9]|[12][0-9]|3[01])/))
	{
		return false;
	}
	try
	{
		var d = date.split(/\D+/);
		d[0]*=1;
		d[1]-=1;
		d[2]*=1;
		var D = new Date(d[0], d[1], d[2]);
		return (
			(D.getFullYear() == d[0]) &&
			(D.getMonth() == d[1]) &&
			(D.getDate() == d[2]) &&
			(d[0] > 1970)
			);
	}
	catch(er)
	{
		return false;
	}

	return false;
}

/*function getCurrentTimestamp() {
	now = new Date();
	year = "" + now.getFullYear();
	month = "" + (now.getMonth() + 1);
	if (month.length == 1) {
		month = "0" + month;
	}
	day = "" + now.getDate();
	if (day.length == 1) {
		day = "0" + day;
	}
	hour = "" + now.getHours();
	if (hour.length == 1) {
		hour = "0" + hour;
	}
	minute = "" + now.getMinutes();
	if (minute.length == 1) {
		minute = "0" + minute;
	}
	second = "" + now.getSeconds();
	if (second.length == 1) {
		second = "0" + second;
	}
	return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
}*/

function format00(x)
{
	result = "" + x;
	if (result.length == 1)
	{
		result = "0" + result;
	}
	return result;
}

function formatTimestamp(time, local)
{
	// 2013-09-07 21:14:38

	if (local)
	{
		year = "" + time.getFullYear();
		month = format00(time.getMonth() + 1);
		day = format00(time.getDate());
		hour = format00(time.getHours());
		minute = format00(time.getMinutes());
		second = format00(time.getSeconds());

		return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
	}
	else
	{
		year = "" + time.getUTCFullYear();
		month = format00(time.getUTCMonth() + 1);
		day = format00(time.getUTCDate());
		hour = format00(time.getUTCHours());
		minute = format00(time.getUTCMinutes());
		second = format00(time.getUTCSeconds());

		return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
	}
}

function formatDate(date, local)
{
	// 2013-09-07
	
	if (local)
	{
		return date.getFullYear() + "-" + format00(date.getMonth()+1) + "-" + format00(date.getDate());
	}
	else
	{
		return date.getUTCFullYear() + "-" + format00(date.getUTCMonth()+1) + "-" + format00(date.getUTCDate());
	}
}

function formatTime(time)
{
	// 21:14

	//var h = format00(Math.floor(time/60));
	//var m = format00(time % 60);
	//return h + ":" + m;
	
	var date = new Date(time + " GMT");
	
	return format00(date.getHours()) + ":" + format00(date.getMinutes());
	//return date;
}

function getCurrentTime()
{
	var currentdate = new Date();
	return currentdate.getHours() * 60 + currentdate.getMinutes();
}

function getCurrentTimestamp()
{
	return formatTimestamp(new Date(), false);
}

function getBefore(str, separator)
{
	var k = str.indexOf(separator);
	return (k == -1 ? str : str.substring(0, k));
}

function getAfter(str, separator)
{
	var k = str.indexOf(separator);
	return (k == -1 ? str : str.substring(k+1, str.length));
}

function strToFloat(s)
{
	if (typeof s == 'string')
		s = parseFloat(s.replace(',', '.'));

	return s;
}

function strToInt(s)
{
	if (typeof s == 'string')
		s = parseInt(s);

	return s;
}

function ObjToSource(o)
{
	if (!o) return 'null';
	var k="",na=typeof(o.length)=="undefined"?1:0,str="";
	for(var p in o)
	{
		if (na) k = "\""+p+ "\":";
		if (typeof o[p] == "string") str += k + "\"" + o[p].replace(/"/g,'\\"')+"\",";
		else if (typeof o[p] == "object") str += k + ObjToSource(o[p])+",";
		else str += k + o[p] + ",";
	}
	if (na) return "{" + str.slice(0, -1) + "}";
	else return "[" + str.slice(0, -1) + "]";
}

//==============================================================================

function xml2Dom(xml)
{
	var dom = null;
	if (window.DOMParser)
	{
		try
		{
			dom = (new DOMParser()).parseFromString(xml, "text/xml");
		}
		catch (e)
		{
			dom = null;
		}
	}
	else
	if (window.ActiveXObject)
	{
		try
		{
			dom = new ActiveXObject('Microsoft.XMLDOM');
			dom.async = false;
			if (!dom.loadXML(xml)) // parse error ..
			{
				window.alert(dom.parseError.reason + dom.parseError.srcText);
			}
		}
		catch (e)
		{
			dom = null;
		}
	}
	else
	{
		alert("cannot parse xml string!");
	}
	return dom;
}

function parseXml(data)
{
	data = getAfter(data, "\n");
	var dom = xml2Dom(data);

	// escape double quotes
	var all = dom.getElementsByTagName("*");
	for (var i=0; i < all.length; i++)
	{
		for (var j = 0; j < all[i].attributes.length; j++)
		{
			all[i].attributes[j].value = all[i].attributes[j].value.replace(/\"/g, '\\"');
		}
	}

	var json = xml2json(dom, "");
	json = json.replace(/undefined/g, "").replace(/@/g, "");
	return JSON.parse(json);
}

function timeSortFunction(a, b)
{
	return a.data.time > b.data.time ? 1 : -1;
}

function nameSortFunction(a, b)
{
	if (a.name < b.name) return -1; // Или любое число, меньшее нуля
	if (a.name > b.name) return +1; // Или любое число, большее нуля
	return 0;
}

function tagSortFunction(a, b)
{
	if (a.tag == b.tag) return nameSortFunction(a, b);
	if (a.tag > b.tag) return -1; // Или любое число, меньшее нуля
	if (a.tag < b.tag) return +1; // Или любое число, большее нуля
	return 0;
}

function generateGuid()
{
	if (typeof (window.crypto) != 'undefined'
			&& typeof (window.crypto.getRandomValues) != 'undefined')
	{
		// If we have a cryptographically secure PRNG, use that
		// http://stackoverflow.com/questions/6906916/collisions-when-generating-uuids-in-javascript
		var buf = new Uint16Array(8);
		window.crypto.getRandomValues(buf);
		var S4 = function(num)
		{
			var ret = num.toString(16);
			while (ret.length < 4)
			{
				ret = "0" + ret;
			}
			return ret;
		};
		return (S4(buf[0]) + S4(buf[1]) + S4(buf[2]) + S4(buf[3])
				+ S4(buf[4]) + S4(buf[5]) + S4(buf[6]) + S4(buf[7]));
	}
	else 
	{
		// Otherwise, just use Math.random
		// http://stackoverflow.com/questions/105034/how-to-create-a-guid-uuid-in-javascript/2117523#2117523
		return 'xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx'.replace(/[xy]/g, function(c)
			{
				var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
				return v.toString(16);
			}
		);
	}
}