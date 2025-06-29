class TableData {
	constructor(link, text, img) {
		this.link = link;
		this.text = text;
		this.img = img;
	}
};

$(document).ready(function () {
	$('#killerstrike_unique').keydown(function (e) {
		if (e.keyCode === 13) {
			openKillstreakUrl($(this).val());
		}
	});
	$('#kit').keydown(function (e) {
		if (e.keyCode === 13) {
			openKitUrl($(this).val());
		}
	});
});

const arg1 = "https://steamcommunity.com/market/search?q=";
const arg2 = "&category_440_Collection%5B%5D=any&category_440_Type%5B%5D=any&category_440_Quality%5B%5D=tag_Unique&appid=440#p1_price_asc";

function openKillstreakUrl(value) {
	window.open(arg1 + value + "+Killstreak" + arg2, '_blank');
}

function openKillstreakUrlEng(value) {
	window.open(arg1 + "\"Killstreak+" + value + "\"" + arg2, '_blank');
}

function openKitUrl(value) {
	window.open(arg1 + value + "+\"team\"+\"Incinerator\"" + "&descriptions=1" + arg2, '_blank');
}