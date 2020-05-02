//DOM

const newPasswordInput = document.querySelector(".new-password");
const repeatInput = document.querySelector(".repeat-new-password");
const changePasswordBtn = document.querySelector(".button");
const form = document.querySelector("form");
const loader = document.querySelector(".loader");

//---------------------------

//Funkcje
handleBtnClick = (e) => {
  e.preventDefault();
  const password = newPasswordInput.value;
  const repeatPassword = repeatInput.value;
  if (
    password === " " ||
    password === null ||
    password === "" ||
    password.length < 6
  ) {
    alert("Pole nie może być puste oraz musi zawierać więcej niż 6 znaków !");
  } else if (
    repeatPassword === " " ||
    repeatPassword === null ||
    repeatPassword === "" ||
    repeatPassword.length < 6
  ) {
    alert("Pole nie może być puste oraz musi zawierać więcej niż 6 znaków !");
  } else if (password !== repeatPassword) {
    alert("Pola musza być identyczne !");
  } else {
    loader.classList.add("active");
    //po zweryfikowaniu danych z "backendu / serwera" -> loader.classList.remove("active");
    setTimeout(function () {
      loader.classList.remove("active");
      alert("Zmiana hasła zakończona sukcesem!");
    }, 3000);
  }
};

//Eventy

changePasswordBtn.addEventListener("click", handleBtnClick);
