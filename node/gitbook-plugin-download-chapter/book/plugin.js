require(["gitbook", "jQuery"], function(gitbook, $) {
  gitbook.events.bind('page.change', function (e, config) {
    var old_button = $(".js-toolbar-action:contains('下载PDF')")
    if (old_button.length>=1) {
      old_button.remove()
    }
    var bookpage = gitbook.page.getState()
    var bookroot = bookpage.bookRoot
    var filepath = bookpage.filepath
    var filepath_part0 = filepath.substring(0,filepath.lastIndexOf("/"))
    var filepath_part1 = filepath.substring(filepath.lastIndexOf("/"))
    if (!filepath_part1.toLowerCase().endsWith('readme.md')) {
      gitbook.toolbar.createButton({
        icon: 'fa fa-file-pdf-o',
        text: "下载PDF",
        onClick: function() {
            window.open(bookroot+filepath_part0 +"/assets"+ filepath_part1.substring(0,filepath_part1.lastIndexOf(".md")) + ".pdf")
        }
      })
    }
  })
})
