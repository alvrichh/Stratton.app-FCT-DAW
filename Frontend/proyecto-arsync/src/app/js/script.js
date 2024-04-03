document.getElementById('big-ball').addEventListener('click', function () {
    const ballsContainer = document.querySelector('.balls-container');
    const ball = document.querySelectorAll('.ball');

    ballsContainer.style.opacity = '1';
    ball.forEach(function (b) {
        b.style.opacity = '1';
    });
});