function CardSlider(sliderId, sliderTrackId) {
    let isDragging = false;
    let startPos = 0;
    let currentTranslate = 0;
    let prevTranslate = 0;
    let animationID = 0;
    let dragTrigger = false;

    let slider= document.getElementById(sliderId);
    let sliderTrack= document.getElementById(sliderTrackId);
    slider.addEventListener('mousedown', dragStart);
    slider.addEventListener('mouseup', dragEnd);
    slider.addEventListener('mouseleave', dragEnd);
    slider.addEventListener('mousemove', drag);

    slider.addEventListener('touchstart', dragStart);
    slider.addEventListener('touchend', dragEnd);
    slider.addEventListener('touchmove', drag);

    document.querySelectorAll(`#${sliderId} .learning-item-card-anchor`).forEach(el => el.addEventListener("dragstart", (e) => e.preventDefault()));
    document.querySelectorAll(`#${sliderId} .learning-item-card-anchor`).forEach(el => el.addEventListener("click", (e) => {
        if (dragTrigger) {
            console.log("drag");
            e.preventDefault();
        } else {
            console.log("click");
        }
    }));

    function dragStart(event) {
        event.preventDefault();

        isDragging = true;
        startPos = getPositionX(event);
        animationID = requestAnimationFrame(animation);
        sliderTrack.style.transition = 'none';
    }

    function drag(event) {
        if (isDragging) {
            const currentPosition = getPositionX(event);
            currentTranslate = (prevTranslate + currentPosition) - startPos;
        }
    }

    function dragEnd(e) {
        e.preventDefault();

        isDragging = false;
        cancelAnimationFrame(animationID);
        let isDrag = false;

        if (currentTranslate > 0) {
            currentTranslate = 0;
            isDrag = true;
        }
        else if (currentTranslate < -2200) {
            currentTranslate = -2200;
            isDrag = true;
        }

        setSliderPosition(isDrag);
        prevTranslate = currentTranslate;

        sliderTrack.style.transition = 'transform 0.3s ease';
    }

    function getPositionX(event) {
        return event.type.includes('mouse') ? event.pageX : event.touches[0].clientX;
    }

    function animation() {
        setSliderPosition();
        if (isDragging) requestAnimationFrame(animation);
    }

    function setSliderPosition(isDrag) {
        if (isDrag) {
            dragTrigger = true;
        } else {
            dragTrigger = prevTranslate !== currentTranslate;
        }
        sliderTrack.style.transform = `translateX(${currentTranslate}px)`;
    }
}