var root = document.getElementById("diary_block");
root.innerHTML = "				<div id=\"diary_info_column\">\n"
		+ "					<div id=\"filler\" style=\"height: 0px\" onclick=\"showInfoBox(-1)\" ></div>\n"
		+ "					<div id=\"diary_info\"></div>\n" + "				</div>\n"
		+ "				<div id=\"diary_page\" class=\"diary_page_empty\"></div>\n";

// компоненты
var calendar = document.getElementById("calendar");
var modspan = document.getElementById("modspan");
var versionspan = document.getElementById("versionspan"); // TODO: unused
var diary = document.getElementById("diary_page");
var infoblock = document.getElementById("diary_info");
var statusImg = document.getElementById("job_img");

// константы
var fingers = ["БЛ", "1Л", "2Л", "3Л", "4Л", "4П", "3П", "2П", "1П", "БП"];
var finger_hints = ["Левая, большой", "Левая, указательный", "Левая, средний",
		"Левая, безымянный", "Левая, мизинец", "Правая, мизинец",
		"Правая, безымянный", "Правая, средний", "Правая, указательный",
		"Правая, большой"];
var PERIOD = 1440;
var BLOOD_ACTUAL_PERIOD = 60;
var INS_ACTUAL_PERIOD = 90;
var TARGET_BS = 5.0; // TODO: load from user properties

var URL_LOGIN_PAGE = "login";

// данные
var cur_date = new Date(document.getElementById("origin_date").value);
var prev_page = [];
var page = [];
var koofs = [];
var foodbase = [];
var dishbase = [];

var fdBase = [];
var selectedItem; // TODO: remove it
var currentID;

var foodbaseLoaded = false;
var dishbaseLoaded = false;

/* ================== STARTUP ACTIONS ================== */

refreshCurrentPage();
// downloadKoofs();
downloadFoodbase();
downloadDishbase();
setProgress("ready", "Дневник сохранён");

/* ================== DIARY NAVIGATION ================== */

function refreshCurrentPage()
{
	downloadPage(shiftDate(cur_date, -1), false);
	prev_page = page;

	downloadPage(cur_date, true);

	// if (DiaryPage_getLastFinger(page) == -1)
	// {

	// }

	showInfoBox(-1);
	calendar.value = formatDate(cur_date, true);
}

function shiftDate(date, days)
{
	var newDate = new Date();
	newDate.setTime(date.getTime() + 86400000 * days);
	return newDate;
}

function prevDay()
{
	cur_date = shiftDate(cur_date, -1);
	pushHistory("index.php?date=" + formatDate(cur_date, false));
	refreshCurrentPage();
}

function nextDay()
{
	cur_date = shiftDate(cur_date, +1);
	pushHistory("index.php?date=" + formatDate(cur_date, false));
	refreshCurrentPage();
}

function openPage()
{
	// TODO: implement
}

/* ================== INTERNET ================== */

function logout()
{
	var url = "api/auth/logout/";

	var onSuccess = function(data)
	{
		document.location = "index.html";
	};

	var onFailure = function()
	{
		document.location = "index.html";
	};

	doGet(url, false, onSuccess, onFailure);
}

function downloadPage(pageDate, show)
{
	var start_time = formatTimestamp(pageDate, false);
	var end_time = formatTimestamp(shiftDate(pageDate, +1), false);
	var url = "api/diary/period/?start_time=" + start_time + "&end_time="
			+ end_time + "&show_rem=0";
	console.log(pageDate);
	console.log(url);

	var onSuccess = function(data)
	{
		var resp = JSON.parse(data);

		if (resp.code == 0)
		{
			var content = JSON.parse(data).resp;
			page = JSON.parse(content.replace('\"', '"'));
			
			page.sort(timeSortFunction);
			
			if (show)
			{
				showPage();
			}
		}
		else if (resp.code == 4011)
		{
			document.location = URL_LOGIN_PAGE;
		}
		else
		{
			alert("Unknown error: " + resp);
		}
	};

	var onFailure = function()
	{
		alert("failure");
	};

	doGet(url, false, onSuccess, onFailure);
}

function downloadKoofs()
{
	var url = "console.php?koofs:download";

	var onSuccess = function(data)
	{
		if (data != "")
		{
			koofs = JSON.parse(data);
		}
		else
		{
			koofs = [];
		}
	};

	var onFailure = function()
	{
		koofs = [];
	};

	doGet(url, true, onSuccess, onFailure);
}

function downloadFoodbase()
{
	var url = "api/food/all?show_rem=0";
	foodbaseLoaded = false;

	var onSuccess = function(data)
	{
		if (data != "")
		{
			foodbase = JSON.parse(data).resp;
			foodbase = JSON.parse(foodbase);

			// TODO: убрать?
			// простановка индексов
			for (var i = 0; i < foodbase.length; i++)
			{
				foodbase[i].data = floatizeFood(foodbase[i].data);
				foodbase[i].index = i;
			}

			foodbaseLoaded = true;
			if (foodbaseLoaded && dishbaseLoaded)
			{
				prepareComboList();
			}
			else
			{
				// console.log("Foodbase downloaded, wait for dishbase...");
			}
		}
		else
		{
			console.log("Foodbase data is empty");
			foodbase = [];
		}
	};

	var onFailure = function()
	{
		console.log("Failed to load foodbase");
		foodbase = [];
	};

	doGet(url, true, onSuccess, onFailure);
}

function downloadDishbase()
{
	var url = "api/dish/all?show_rem=0";
	dishbaseLoaded = false;

	/*var onSuccess = function(data)
	{
		if (data != "")
		{
			dishbase = parseXml(data).dishes;

			// TODO: убрать?
			// простановка индексов
			for (var i = 0; i < dishbase.dish.length; i++)
			{
				dishbase.dish[i] = floatizeDish(dishbase.dish[i]);
				dishbase.dish[i].id = i;
			}

			dishbaseLoaded = true;
			if (foodbaseLoaded && dishbaseLoaded)
			{
				prepareComboList();
			}
			else
			{
				// console.log("Dishbase downloaded, wait for foodbase...");
			}
		}
		else
		{
			console.log("Dishbase data is empty");
			dishbase = [];
		}
	};

	var onFailure = function()
	{
		console.log("Failed to load dishbase");
		dishbase = [];
	};

	doGet(url, true, onSuccess, onFailure);*/
	
	dishbaseLoaded = true;
}

function floatizeFood(food)
{
	food.prots = strToFloat(food.prots);
	food.fats = strToFloat(food.fats);
	food.carbs = strToFloat(food.carbs);
	food.val = strToFloat(food.val);
	food.tag = strToInt(food.tag);
	return food;
}

function floatizeDish(dish)
{
	if (!('item' in dish))
	{
		dish.item = [];
	}
	else if (!dish.item.length)
	{
		var temp = [];
		temp.push(dish.item);
		dish.item = temp;
	}

	for (var i = 0; i < dish.item.length; i++)
	{
		dish.item[i].prots = strToFloat(dish.item[i].prots);
		dish.item[i].fats = strToFloat(dish.item[i].fats);
		dish.item[i].carbs = strToFloat(dish.item[i].carbs);
		dish.item[i].val = strToFloat(dish.item[i].val);
		dish.item[i].mass = strToFloat(dish.item[i].mass);
	}

	if ('mass' in dish)
	{
		dish.mass = strToFloat(dish.mass);
	}
	dish.tag = strToInt(dish.tag);

	return dish;
}

function dishAsFood(dish)
{
	var summProts = 0.0;
	var summFats = 0.0;
	var summCarbs = 0.0;
	var summVal = 0.0;
	var summMass = 0.0;

	for (var k = 0; k < dish.item.length; k++)
	{
		summProts += dish.item[k].mass * dish.item[k].prots / 100;
		summFats += dish.item[k].mass * dish.item[k].fats / 100;
		summCarbs += dish.item[k].mass * dish.item[k].carbs / 100;
		summVal += dish.item[k].mass * dish.item[k].val / 100;
		summMass += dish.item[k].mass;
	}

	var dishMass;
	if ('mass' in dish)
	{
		dishMass = dish.mass;
	}
	else
	{
		dishMass = summMass;
	}

	var food = {};
	food.name = dish.name;
	food.tag = dish.tag;
	food.prots = (summProts / dishMass * 100).toFixed(2);
	food.fats = (summFats / dishMass * 100).toFixed(2);
	food.carbs = (summCarbs / dishMass * 100).toFixed(2);
	food.val = (summVal / dishMass * 100).toFixed(2);

	return food;
}

function prepareComboList()
{
	fdBase = [];
	var item;

	// adding foods
	for (var i = 0; i < foodbase.length; i++)
	{
		item = {};
		item.value = foodbase[i].data.name;
		item.prots = foodbase[i].data.prots;
		item.fats = foodbase[i].data.fats;
		item.carbs = foodbase[i].data.carbs;
		item.val = foodbase[i].data.val;
		item.tag = foodbase[i].data.tag;
		item.type = "food";
		
		fdBase.push(item);
	}

	// adding dishes
	for (var j = 0; j < dishbase.length; j++)
	{
		var cnv = dishAsFood(dishbase[j]);

		item = {};
		item.value = cnv.name;
		item.prots = cnv.prots;
		item.fats = cnv.fats;
		item.carbs = cnv.carbs;
		item.val = cnv.val;
		item.tag = cnv.tag;
		item.type = "dish";
		fdBase.push(item);
	}

	fdBase.sort(tagSortFunction);
	createHandlers();
}

function uploadPage(uploadedItem)
{
	var url = "api/diary/";
	var items = [];
	items.push(uploadedItem);
	var request = 'items='
			+ encodeURIComponent(ObjToSource(items));

	var onSuccess = function(resp)
	{
		// document.getElementById("summa").innerHTML = xmlhttp.responseText; //
		// Выводим ответ сервера
		
		var json = JSON.parse(resp);
		
		if (json.code == 0)
		{
			setProgress("ready", "Дневник сохранён");
		}
		else if (json.code == 4011)
		{
			document.location = URL_LOGIN_PAGE;
		}
		else
		{
			setProgress("error", "Не удалось сохранить дневник");
			alert("Failed to save with message '" + resp + "'");
		}
	};

	var onFailure = function()
	{
		setProgress("error", "Не удалось сохранить дневник");
		alert("Failed to save page");
	};

	setProgress("progress", "Идёт сохранение...");
	doPut(url, request, true, onSuccess, onFailure);
}

/* ================== KOOF UTILS ================== */

function getR(left, right, time)
{
	if (left > right)
		right += PERIOD;
	if (time < left)
		time += PERIOD;
	if (time > right)
		time -= PERIOD;

	return (time - left) / (right - left);
}

function interpolateBi(v1, v2, time)
{
	var r = getR(v1.time, v2.time, time);

	var _k = (v1.k * (1 - r) + v2.k * r).toFixed(4);
	var _q = (v1.q * (1 - r) + v2.q * r).toFixed(2);
	var _p = (v1.p * (1 - r) + v2.p * r).toFixed(2);

	return {
		k : _k,
		q : _q,
		p : _p
	};
}

function interpolate(koofList, time)
{
	if ((koofList == null) || (koofList.length == 0))
		return null;

	if ((time < koofList[0].time)
			|| (time >= koofList[koofList.length - 1].time))
		return interpolateBi(koofList[koofList.length - 1], koofList[0], time);

	for (var i = 0; i < koofList.length - 1; i++)
		if ((time >= koofList[i].time) && (time < koofList[i + 1].time))
			return interpolateBi(koofList[i], koofList[i + 1], time);

	return null;
}

/* ================== PRINTERS (PAGE) ================== */

function codeBlood(blood, id)
{
	var value = strToFloat(blood.value).toFixed(1);
	var finger = fingers[blood.finger];
	var finger_hint = finger_hints[blood.finger];

	if (null == finger)
		finger = "";
	if (null == finger_hint)
		finger_hint = "";

	return '' + '				<div id="diaryRec_'
			+ id
			+ '" class="rec blood" onclick="onRecordClick(this.id)">\n'
			+ '					<div class="time hoverable" id="time_'
			+ id
			+ '" onclick="onTimeClick(this.id)">'
			+ formatTime(blood.time)
			+ "</div>\n"
			+ '					<div class="item">'
			+ "						<table cellpadding=\"0\">\n"
			+ "							<tr>\n"
			+ '								<td class="col_item"><span id="item_'
			+ id
			+ '" class="hoverable" onclick="onBloodClick(this.id)">'
			+ value
			+ ' ммоль/л</span></td>\n'
			+ "								<td class=\"col_info\"><div id=\"item_"
			+ id
			+ "\" title=\""
			+ finger_hint
			+ "\">"
			+ finger
			+ "</div></td>\n"
			+ "								<td class=\"col_delete\"><div id=\"item_"
			+ id
			+ "\" onclick=\"onDeleteClick(this.id)\" title=\"Удалить\">X</div></td>\n"
			+ "							</tr>\n" + "						</table>\n" + '					</div>'
			+ '				</div>';
}

function codeIns(ins, id)
{
	console.log("building ins with id " + id);
	return '' + '				<div id="diaryRec_'
			+ id
			+ '" class="rec ins" onclick="onRecordClick(this.id)">\n'
			+ '					<div class="time hoverable" id="time_'
			+ id
			+ '" onclick="onTimeClick(this.id)">'
			+ formatTime(ins.time)
			+ "</div>\n"
			+ '					<div class="item">'
			+ "						<table cellpadding=\"0\">\n"
			+ "							<tr>\n"
			+ '								<td class="col_item"><span id="item_'
			+ id
			+ '" class="hoverable" onclick="onInsClick(this.id)">'
			+ ins.value
			+ ' ед</span></td>\n'
			+ "								<td class=\"col_info\"></td>\n"
			+ "								<td class=\"col_delete\"><div id=\"item_"
			+ id
			+ "\" onclick=\"onDeleteClick(this.id)\" title=\"Удалить\">X</div></td>\n"
			+ "							</tr>\n" + "						</table>\n" + '					</div>'
			+ '				</div>';
}

function codeMeal(meal, id)
{
	var t = (meal.short ? "short_meal" : "meal");
	var code = '';
	code += '				<div id="diaryRec_' + id + '" class="rec ' + t
			+ '" onclick="onRecordClick(this.id)">\n';

	code += '					<div class="time hoverable" id="time_' + id
			+ '" onclick="onTimeClick(this.id)">' + formatTime(meal.time)
			+ "</div>\n" + '					<div class="item">\n'
			+ "						<table class=\"meal_table\">\n";
	for (var i = 0; i < meal.content.length; i++)
	{
		code += "							<tr class=\"food\">\n"
				+ "								<td class=\"col_item\"><span id=\"food_"
				+ id
				+ "_"
				+ i
				+ "\">"
				+ meal.content[i].name
				+ "</span></td>\n"
				+ '								<td class="col_info"><div id="food_'
				+ id
				+ '_'
				+ i
				+ '" class="hoverable" onclick="onFoodMassClick(this.id)" title="Изменить массу">'
				+ meal.content[i].mass
				+ '</div></td>\n'
				+ "								<td class=\"col_delete\"><div id=\"food_"
				+ id
				+ "_"
				+ i
				+ "\" onclick=\"onRemoveFoodClick(this.id)\" title=\"Удалить\">X</div></td>\n"
				+ "							</tr>\n";

		// code += codeFood(meal.content[i], id + "_" + i) + '<br/>\n';
	}

	code += "							<tr class=\"food meal_adder\">\n"
			+ "								<td class=\"col_item\">\n"
			+ "									<div class=\"wrapper_table\">\n"
			+ "										<span class=\"wrapper_cell\">\n"
			+ '											<input id="mealcombo_'
			+ id
			+ '" class="meal_input full_width bold '
			+ t
			+ '" placeholder="Введите название..."/>\n'
			+ "										</span>\n"
			+ "									</div>\n"
			+ '								</td>\n'
			+ '								<td class="col_info">\n'
			+ '									<div class="wrapper_table">\n'
			+ '										<span class="wrapper_cell">\n'
			+ '											<input id="mealmass_'
			+ id
			+ '" class="meal_input full_width bold '
			+ t
			+ '" type="number" placeholder="..." title="Масса" onkeypress="onMassKeyDown(event,'
			+ id
			+ ')"/>\n'
			+ '										</span>\n'
			+ '									</div>\n'
			+ '								</td>\n'
			+ "								<td class=\"col_delete\"><button class=\"meal_add\" onclick=\"addItemToMeal("
			+ id + ")\" title=\"Добавить\"></button></td>\n" + "							</tr>\n";

	code += "						</table>\n" + '					</div>\n' + '				</div>\n';
	return code;
}

function codeNote(note, id)
{
	return '' + '				<div id="diaryRec_'
			+ id
			+ '" class="rec note" onclick="onRecordClick(this.id)">\n'
			+ '					<div class="time hoverable" id="time_'
			+ id
			+ '" onclick="onTimeClick(this.id)">'
			+ formatTime(note.time)
			+ "</div>\n"
			+ '					<div class="item">'
			+ "						<table cellpadding=\"0\">\n"
			+ "							<tr>\n"
			+ '								<td class="col_item"><span id="item_'
			+ id
			+ '" class="hoverable" onclick="onNoteClick(this.id)">'
			+ note.text
			+ '</span></td>\n'
			+ "								<td class=\"col_info\"></td>\n"
			+ "								<td class=\"col_delete\"><div id=\"item_"
			+ id
			+ "\" onclick=\"onDeleteClick(this.id)\" title=\"Удалить\">X</div></td>\n"
			+ "							</tr>\n" + "						</table>\n" +

			'					</div>' + '				</div>';
}

function codePage(page)
{
	var code = '';// ' <div class="diary">';
	for (var i = 0; i < page.length; i++)
	{
		if (page[i].deleted != "true")
		{
			console.log("processing item #" + i);

			if (page[i].data.type == "meal")
				code += codeMeal(page[i].data, i);
			else if (page[i].data.type == "blood")
				code += codeBlood(page[i].data, i);
			else if (page[i].data.type == "ins")
				code += codeIns(page[i].data, i);
			else if (page[i].data.type == "note")
				code += codeNote(page[i].data, i)
			else
				console.log("Unknown item type: " + page[i].data.type);
		}
	}
	// code += ' </div>';
	/*
	 * code += ' <div class="ui-widget">\n' + ' <span id="meal"/>\n' + '
	 * <br/>\n' + ' <label for="fdAutocomplete">Выберите продукт или блюдо:</label>\n' + '
	 * <div>\n' + ' <div id="block_ok">\n' + ' <button title="Добавить
	 * (Enter)">+</button>\n' + ' </div>\n' + ' <div id="block_mass">\n' + '
	 * <input class="myinput" id="mass"\n' + ' onkeypress="return
	 * onMassKeyDown(event)" type="number"/>\n' + ' </div>\n' + ' <div
	 * id="block_fd">\n' + ' <input class="myinput" id="fdAutocomplete"/>\n' + '
	 * </div>\n' + ' </div>\n' + ' </div>';
	 */
	return code;
}

function createHandlers()
{
	for (var i = 0; i < page.length; i++)
	{
		if (page[i].data.type == "meal")
		{
			var id = i;
			// вешаем обработчики
			$(function()
			{
				$("#mealcombo_" + id).autocomplete(
				{
					autoFocus : true,
					source : fdBase,
					delay : 0,
					minLength : 2
				});
			});
			$("#mealcombo_" + id).on("autocompleteselect", function(event, ui)
			{
				selectedItem = ui.item;
				var t = 'mealmass_' + currentID;
				var mc = document.getElementById(t);
				// console.log("Searching for component '" + t + "': " + mc);
				mc.focus();
			});
		}
	}
}

function showPage()
{
	if (page.length > 0)
	{
		diary.className = "diary_page_full";
		diary.innerHTML = codePage(page);
		//var stamp = new Date(Date.parse(page.timestamp + " UTC"));
		//modspan.innerHTML = formatTimestamp(stamp, true).replace(" ", "<br/>");
		//versionspan.innerHTML = "";// "#" + page.version;
		createHandlers();
	}
	else
	{
		diary.className = "diary_page_empty";
		diary.innerHTML = "Страница пуста";
		modspan.innerHTML = "";
		versionspan.innerHTML = "";
	}
}

/* ================== PRINTERS (INFO PANEL) ================== */

function codeNewRecord()
{
	return ''
			+ "						<div>\n"
			+ "							<div onclick=\"newBloodEditor()\" class=\"button_new_rec button_new_blood\">Замер СК</div>\n"
			+ "							<div onclick=\"newInsEditor()\" class=\"button_new_rec button_new_ins\">Инъекция</div>\n"
			+ "							<div onclick=\"newMealEditor()\" class=\"button_new_rec button_new_meal\">Приём пищи</div>\n"
			+ "							<div onclick=\"newNoteEditor()\" class=\"button_new_rec button_new_note\">Заметка</div>\n"
			+ "						</div>\n";
}

function codeInfoDefault()
{
	return codeNewRecord()
			+ "<hr>\n"
			+ "						Для получения более подробной информации выберите запись на странице.\n";
}

function codeInfoBlood(rec)
{
	return '' + "						Вы выбрали замер СК<br/><br/><hr>\n"
			+ "						Добавить новую запись:<br/><br/>\n" + codeNewRecord();
}

function codeInfoIns(rec)
{
	return '' + "						Вы выбрали инъекцию<br/><br/><hr>\n"
			+ "						Добавить новую запись:<br/><br/>\n" + codeNewRecord();
}

function codeInfoMeal(meal)
{
	var prots = 0;
	var fats = 0;
	var carbs = 0;
	var val = 0;
	for (var i = 0; i < meal.content.length; i++)
	{
		prots += strToFloat(meal.content[i].prots)
				* strToFloat(meal.content[i].mass) / 100;
		fats += strToFloat(meal.content[i].fats)
				* strToFloat(meal.content[i].mass) / 100;
		carbs += strToFloat(meal.content[i].carbs)
				* strToFloat(meal.content[i].mass) / 100;
		val += strToFloat(meal.content[i].value)
				* strToFloat(meal.content[i].mass) / 100;
	}

	var w = interpolate(koofs, meal.time);
	var blood = DiaryPage_findBlood(meal.time, BLOOD_ACTUAL_PERIOD);
	var BsTarget = TARGET_BS;
	var BsInput = (blood == null ? BsTarget : strToFloat(blood.value));
	var dose = null;
	var ins = DiaryPage_findIns(meal.time, INS_ACTUAL_PERIOD);
	var injectedDose = (ins == null ? null : strToFloat(ins.value).toFixed(1));
	var dk = null;
	var correctionClass = "";

	if (w != null)
	{
		dose = ((BsInput - BsTarget + w.k * carbs + w.p * prots) / w.q)
				.toFixed(1);
		if (injectedDose != null)
		{
			dk = ((BsTarget - BsInput - w.p * prots + w.q * injectedDose) / w.k - carbs)
					.toFixed(0);
			if (Math.abs(dk) < 0.51)
				dk = 0;
			if (dk > 0.5)
				correctionClass = "positive";
			else if (dk < -0.5)
				correctionClass = "negative";
			if (dk > 0)
				dk = "+" + dk;
		}
	}

	if (injectedDose == null)
		injectedDose = "?";
	if (dose == null)
		dose = "?";
	if (dk == null)
		dk = "?";

	return '' + '<table cellpadding="1" style="text-align: left">\n'
			+ '							<tr>\n'
			+ '								<th class="full_width">Белки, г</th>\n'
			+ '								<td class="right">'
			+ prots.toFixed(0)
			+ '</td>\n'
			+ '							</tr>\n'
			+ '							<tr>\n'
			+ '								<th class="full_width">Жиры, г</th>\n'
			+ '								<td class="right">'
			+ fats.toFixed(0)
			+ '</td>\n'
			+ '							</tr>\n'
			+ '							<tr>\n'
			+ '								<th class="full_width">Углеводы, г</th>\n'
			+ '								<td class="right">'
			+ carbs.toFixed(0)
			+ '</td>\n'
			+ '							</tr>\n'
			+ '							<tr>\n'
			+ '								<th class="full_width">Ценность, ккал</th>\n'
			+ '								<td class="right">'
			+ val.toFixed(0)
			+ '</td>\n'
			+ '							</tr>\n'
			+ '						</table>\n'
			+ '						<hr>\n'
			+ '						<table cellpadding="1" style="text-align: left">\n'
			+ '							<tr>\n'
			+ '								<th class="full_width">Доза введённая, ед</th>\n'
			+ '								<td class="right">'
			+ injectedDose
			+ '</td>\n'
			+ '							</tr>\n'
			+ '							<tr>\n'
			+ '								<th class="full_width">Доза рассчётная, ед</th>\n'
			+ '								<td class="right">'
			+ dose
			+ '</td>\n'
			+ '							</tr>\n'
			+ '							<tr>\n'
			+ '								<th class="full_width">Коррекция, г</th>\n'
			+ '								<td class="right '
			+ correctionClass
			+ '">'
			+ dk
			+ '</td>\n'
			+ '							</tr>\n'
			+ '						</table>\n'
			+ '						<br>\n'
			+ '						<div class="hint">\n'
			+ '							<a href="#" onclick="switchMealInfo(false);">Таблица подбора</a> &gt;\n'
			+ '						</div>';
}

function codeInfoNote(rec)
{
	return '' + "						Вы выбрали заметку.<br/><br/><hr>\n"
			+ "						Добавить новую запись:<br/><br/>\n" + codeNewRecord();
}

/*
 * ================================ INFO PANEL UTILS
 * ================================
 */

function doMove(startHeight, targetHeight, startTime, duration)
{
	var currentTime = new Date().getTime();

	var e = document.getElementById('filler');
	// var current = parseInt(getBefore(e.style.height, "px"));

	if (currentTime > startTime + duration)
	{
		e.style.height = targetHeight + "px";
		return;
	}

	var k = (currentTime - startTime) / duration;
	var height = startHeight * (1 - k) + targetHeight * k;

	e.style.height = height + "px";
	setTimeout("doMove(" + startHeight + "," + targetHeight + "," + startTime
			+ "," + duration + ")", 2);
}

function moveInfoBox(index)
{
	var targetHeight = 0;
	for (var i = 0; i < index; i++)
	{
		if (page[i].deleted != "true")
		{
			targetHeight += document.getElementById("diaryRec_" + i).offsetHeight;
		}
	}
	// document.getElementById('filler').setAttribute("style","height:" +
	// targetHeight + "px");
	// document.getElementById('filler').style.height = targetHeight + "px";
	var startHeight = getBefore(document.getElementById('filler').style.height,
			"px");
	doMove(startHeight, targetHeight, new Date().getTime(), 200);
}

function showInfoBox(index)
{
	currentID = index;
	moveInfoBox(index);

	if ((index < 0) || (index >= page.length))
		infoblock.innerHTML = codeInfoDefault();
	else if (page[index].data.type == "blood")
		infoblock.innerHTML = codeInfoBlood(page[index].data);
	else if (page[index].data.type == "ins")
		infoblock.innerHTML = codeInfoIns(page[index].data);
	else if (page[index].data.type == "meal")
		infoblock.innerHTML = codeInfoMeal(page[index].data);
	else if (page[index].data.type == "note")
		infoblock.innerHTML = codeInfoNote(page[index].data);
	else
		infoblock.innerHTML = codeInfoDefault();

}

function setProgress(status, hint)
{
	if (status == "progress")
	{
		statusImg.setAttribute("display", "block");
		statusImg.setAttribute("src", "res/img/status-progress.gif");
	}
	else if (status == "ready")
	{
		statusImg.setAttribute("display", "block");
		statusImg.setAttribute("src", "res/img/status-ready.png");
	}
	else if (status == "error")
	{
		statusImg.setAttribute("display", "block");
		statusImg.setAttribute("src", "res/img/status-error.png");
	}
	else if (status == "none")
	{
		statusImg.setAttribute("display", "none");
		statusImg.setAttribute("src", "");
	}

	statusImg.setAttribute("title", hint);
}

/* ================== DIARY PAGE METHODS ================== */

function modified(index, needResort)
{
	page[index].version++;
	page[index].stamp = getCurrentTimestamp();
	uploadPage(page[index]);
	
	if (needResort)
		page.sort(timeSortFunction);

	showPage();
}

function DiaryPage_addRecord(rec)
{
	var item = {};
	item.version = 0;
	item.id = generateGuid();
	item.deleted = "false";
	item.data = rec;
	page.push(item);
	modified(page.length - 1, true);
}

function DiaryPage_deleteRecord(index)
{
	page[index].deleted = "true";
	modified(index, false);
}

function DiaryPage_changeTime(index, newTime)
{
	page[index].data.time = formatTimestamp(newTime, false);
	modified(index, true);
}

function DiaryPage_changeFoodMass(mealIndex, foodIndex, newMass)
{
	page[mealIndex].data.content[foodIndex].mass = newMass;
	modified(mealIndex, false);
}

function DiaryPage_removeFood(mealIndex, foodIndex)
{
	page[mealIndex].data.content.splice(foodIndex, 1);
	if (page[mealIndex].data.content.length == 0)
	{
		// удаляем также пустой приём пищи
		page.splice(mealIndex, 1);
	}
	modified(mealIndex, false);
}

function DiaryPage_getLastFinger(page)
{
	for (var i = page.length - 1; i >= 0; i--)
	{
		if (page[i].data.type == "blood")
			return parseInt(page[i].data.finger);
	}
	return -1;
}

function DiaryPage_findRec(type, time, maxDist)
{
	var min = maxDist + 1;
	var rec = null;

	for (var i = 0; i < page.length; i++)
	{
		if (page[i].data.type == type)
		{
			var cur = Math.abs(page[i].data.time - time);
			if (cur < min)
			{
				min = cur;
				rec = page[i];
			}
		}
	}
	return rec;
}

function DiaryPage_findBlood(time, maxDist)
{
	return DiaryPage_findRec("blood", time, maxDist);
}

function DiaryPage_findIns(time, maxDist)
{
	return DiaryPage_findRec("ins", time, maxDist);
}

/* ================== EVENT HANDLERS ================== */

function newBloodEditor()
{
	var finger = DiaryPage_getLastFinger(page);
	if (finger == -1)
		finger = DiaryPage_getLastFinger(prev_page);
	finger = (finger + 1) % 10;

	var val = inputFloat("Введите значение СК (" + finger_hints[finger] + "):",
			"");

	if (val > -1)
	{
		var blood_rec = {};
		blood_rec.type = "blood";
		blood_rec.finger = finger;
		blood_rec.time = getCurrentTimestamp();
		blood_rec.value = String(val);
		DiaryPage_addRecord(blood_rec);
	}
}

function newInsEditor()
{
	var val = inputFloat("Введите дозу инсулина", "");

	if (val > -1)
	{
		var ins_rec = {};
		ins_rec.type = "ins";
		ins_rec.time = getCurrentTimestamp();
		ins_rec.value = String(val);
		DiaryPage_addRecord(ins_rec);
	}
}

function newMealEditor()
{
	var oldTime = new Date();
	var newTime = inputTime("Введите время:", oldTime);
	if (newTime != null)
	{
		xTime = new Date(oldTime);
		xTime.setHours(newTime.getHours(), newTime.getMinutes(), 0, 0);
		
		var meal_rec = {};
		meal_rec.type = "meal";
		meal_rec.time = formatTimestamp(xTime, false);
		meal_rec.content = [];
		meal_rec.short = false;

		DiaryPage_addRecord(meal_rec);
	}
}

function newNoteEditor()
{
	var val = inputText("Введите заметку:");

	if (val != null)
	{
		var note_rec = {};
		note_rec.type = "note";
		note_rec.text = val;
		note_rec.time = getCurrentTimestamp();
		DiaryPage_addRecord(note_rec);
	}
}

function onDateChanged(datePicker)
{
	var date = datePicker.value + " 00:00:00";
	
	console.log("Date selected: " + date);
	
	if (valid(date))
	{
		cur_date = new Date(date);
		// loadDoc(document.getElementById("diary_page"),
		// "console.php?diary:download&dates=" + cur_date);
		// window.location='index.php?date=' + date;
		refreshCurrentPage();
	}
}

function onTimeClick(id)
{
	var index = getAfter(id, "_");
	var oldTime = new Date(page[index].data.time);
	var newTime = inputTime("Введите время:", oldTime);
	if (newTime != null)
	{
		xTime = oldTime;
		xTime.setHours(newTime.getHours(), newTime.getMinutes(), 0, 0);
		DiaryPage_changeTime(index, xTime);
	}
}

function onDeleteClick(id)
{
	var index = getAfter(id, "_");
	if (confirm("Удалить запись?"))
	{
		DiaryPage_deleteRecord(index);
	}
}

function onBloodClick(id)
{
	var index = getAfter(id, "_");
	var oldValue = page[index].data.value;
	var newValue = inputFloat("Введите значение СК:", oldValue);
	if (newValue > 0)
	{
		page[index].data.value = newValue;
		modified(index, false);
	}
}

function onInsClick(id)
{
	var index = getAfter(id, "_");
	var oldValue = page[index].data.value;
	var newValue = prompt("Введите дозу:", oldValue);
	if ((newValue != null) && (newValue > 0))
	{
		page[index].data.value = newValue;
		modified(index, false);
	}
}

function onNoteClick(id)
{
	var index = getAfter(id, "_");
	var oldText = page[index].data.text;
	var newText = prompt("Введите заметку:", oldText);
	if (newText != null)
	{
		page[index].data.text = newText;
		modified(index, false);
	}
}

function onRecordClick(id)
{
	showInfoBox(getAfter(id, "_"));
}

function onFoodMassClick(id)
{
	id = getAfter(id, "_");
	var mealIndex = getBefore(id, "_");
	var foodIndex = getAfter(id, "_");

	var oldMass = page[mealIndex].data.content[foodIndex].mass;
	var name = page[mealIndex].data.content[foodIndex].name;
	var newMass = inputFloat(name + " (г):", oldMass);
	if (newMass != -1)
	{
		DiaryPage_changeFoodMass(mealIndex, foodIndex, newMass);
		// info panel updates automatically due to inherited clicking
	}
}

function onRemoveFoodClick(id)
{
	id = getAfter(id, "_");
	var mealIndex = getBefore(id, "_");
	var foodIndex = getAfter(id, "_");

	if (confirm("Удалить «" + page[mealIndex].data.content[foodIndex].name
			+ "» ?"))
	{
		DiaryPage_removeFood(mealIndex, foodIndex);
		// info panel updates automatically due to inherited clicking
	}
}

function pushHistory(url)
{
	// "index.php?date=2013-04-01"
	// TODO: what does these parameters mean?
	var stateObj =
	{
		foo : "bar"
	};
	history.pushState(stateObj, "Компенсация", url);
}

function onMassKeyDown(e, id)
{
	if (e.keyCode == 13)
	{
		addItemToMeal(id);
	}
	return false;
}

function addItemToMeal(id)
{
	var component_combo = document.getElementById('mealcombo_' + id);
	var component_mass = document.getElementById("mealmass_" + id);

	var item = {};
	item.name = selectedItem.value;
	item.prots = selectedItem.prots;
	item.fats = selectedItem.fats;
	item.carbs = selectedItem.carbs;
	item.value = selectedItem.val;
	item.mass = strToFloat(component_mass.value);

	if (item.mass >= 0)
	{
		// console.log("Add " + ObjToSource(item) + " to meal #" + id);
		page[id].data.content.push(item);
		modified(id, false);

		// пост-настройка интерфейса
		showInfoBox(id);

		// после рендеринга страницы компоненты нужно найти заново
		component_combo = document.getElementById('mealcombo_' + id);
		component_mass = document.getElementById("mealmass_" + id);

		component_combo.value = "";
		component_combo.focus();
		component_mass.value = "";
	}
	else
	{
		alert("Введите корректную неотрицательную массу");
		component_mass.focus();
		component_mass.select();
	}
}