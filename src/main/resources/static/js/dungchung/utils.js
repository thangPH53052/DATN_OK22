// Hàm để cuộn lên đầu trang
function gotoTop() {
  if (window.jQuery) {
    jQuery('html,body').animate({
      scrollTop: 0
    }, 300);
  } else {
    document.documentElement.scrollTop = 0;
  }
}

// Hiển thị nút "Về đầu trang" khi cuộn xuống
window.addEventListener('scroll', function() {
  if (document.documentElement.scrollTop > 500) {
    document.getElementById('goto-top-page').style.display = 'block';
  } else {
    document.getElementById('goto-top-page').style.display = 'none';
  }
});