const ImageSlider = (() => {
    const that = {};
    let nowPage = 1;
    let totalPages = 0;

    // 하단 버튼 동작
    function changedBottomButton(index) {
        const bottomSliderBar = document.getElementById("image-slider-bar-container");
        bottomSliderBar.querySelector(".image-slider-bar.active")?.classList.remove("active");
        [...bottomSliderBar.querySelectorAll(".image-slider-bar")][index - 1]?.classList.add("active");
    }

    // 이미지 변경
    function changedImage(index) {
        const imageSliderItems = document.getElementById("image-slider-items");
        let left = index * 1250;
        imageSliderItems.style.left = `-${left}px`;
    }

    that.init = () => {
        totalPages = document.querySelectorAll("#image-slider-items .image-slider-item").length;
        const imageSliderNext = document.getElementById("image-slider-next");
        const imageSliderPrev = document.getElementById("image-slider-prev");

        imageSliderNext.addEventListener("click", (e) => {
            if (++nowPage > totalPages) nowPage = 1;

            changedBottomButton(nowPage); // 하단 버튼
            changedImage(nowPage - 1); // 이미지
        })

        imageSliderPrev.addEventListener("click", (e) => {
            if (--nowPage < 1) nowPage = totalPages;

            changedBottomButton(nowPage); // 하단 버튼
            changedImage(nowPage - 1); // 이미지
        })

        // 일정 시간동안 자동 변경
        setInterval(() => imageSliderNext.click(), 3000);
    }

    return that;
})()