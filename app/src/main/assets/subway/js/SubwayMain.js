$(document).ready(function() {
	// DatePicker 4일 전으로 value 초기화
	window.Android.initDate();

	// 날짜가 변경될 때 데이터 송수신
	$("#datePicker").change(function() {
		var date = $("#datePicker").val().split("-").join("");
		window.Android.changeDate(date);
		initView();
	});

	// searchBtn Click Event
	$("#searchBtn").click(function() {
		var keyword = $("#inpKeyword").val();
		if (!keyword) alert("역명을 입력해주세요!");
		else window.Android.search(keyword);
	});

	// input Enter Key Event
	$("#inpKeyword").keydown(function(e) {
		if (e.keyCode == 13) {
			$("#searchBtn").click();
		}
	});

	// resetBtn Click Event
	$("#resetBtn").click(function() {
		if (confirm("검색 결과를 초기화 하시겠습니까?")) initView();
	});
});

var initView = function() {
	$(".resultTxt").text("검색 결과가 표시됩니다.");
	$("#inpKeyword").val("");
};

/** [multiLine 개행] */
$.fn.multiLine = function(text) {
	this.text(text);
	this.html(this.html().replace(/\n/g, '<br/>'));
	return this;
};

/** [initDate Native 날짜 데이터 변환 및 초기화] */
var getInitDate = function(date) {
	var year = date.substring(0, 4);	// 년
	var month = date.substring(4, 6);	// 월
	var day = date.substring(6, 8);		// 일
	var result = year + "-" + month + "-" + day;
	$("#datePicker").val(result);
};

/** [getNativeData 네이티브에서 받은 결과값 SET] */
var getNativeData = function(result) {
	if (!result) {
		alert("검색결과가 없습니다.");
		$("#inpKeyword").val("");
	} else {
		$(".resultTxt").text(result).multiLine(result);
	}
};
