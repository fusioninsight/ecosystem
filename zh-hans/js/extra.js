window.onload = function(){
  document.getElementsByClassName('md-nav__title--site')[0].innerHTML ='<a href="https://fusioninsight.github.io/ecosystem/zh-hans" title="FusionInsight MRS 生态地图">FusionInsight MRS 生态地图</a>'
  document.getElementsByClassName('md-header-nav__title')[0].innerHTML = '<a href="https://fusioninsight.github.io/ecosystem/zh-hans" title="FusionInsight MRS 生态地图">FusionInsight MRS 生态地图</a><a style="margin-left: 20px;font-size: .55rem;color: #000000ba;border-radius: 2px;background-color: #ffffff75;" href="https://fusioninsight.github.io/ecosystem/en">en</a>'

  var scrolldiv = $('.details')[0].scrollTop;
var existheader = $('.tg >thead');
if (existheader.length>=1) {
	var header = $('.tg >thead')[0];
	var sticky = $('.tg >thead').offset().top;
	fixnodes_width = []
	fixnodes =  $('.tg > thead > tr > td')
	for (var k = 0, length = fixnodes.length; k < length; k++) {
		fixnodes_width.push(fixnodes[k].offsetWidth)
	}
	$(document).on('scroll',function(){
	  if ($('.details')[0].scrollTop >= sticky - 55 ) {
		if (!document.getElementById('clonehead')) {
		  var tg = $('.tg')[0];
		  var newnode = header.cloneNode(true);
		  newnode.classList.add("sticky");
		  newnode.setAttribute("id", "clonehead");
		  tg.insertBefore(newnode, header);
		  var newnode_tds = $('#clonehead > tr > td');
		  for (var k = 0, length = newnode_tds.length; k < length; k++) {
			newnode_tds[k].width = fixnodes_width[k]
		  }
		}
	  } else {
		if (document.getElementById('clonehead')) {
		  document.getElementById('clonehead').remove();
		}
	  }
	});
}
}
