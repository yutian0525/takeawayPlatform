export function getCurDate(){
    let date = new Date();
    let year = date.getFullYear();
    let month = date.getMonth() + 1;
    let day = date.getDate();

    month = month < 10 ? '0'+month : month;
    day = day < 10 ? '0'+day : day;

    return `${year}-${month}-${day}`
}

export const setSessionStorage = (key, value) => {
  sessionStorage.setItem(key, JSON.stringify(value));
};

export const getSessionStorage = (key) => {
  return sessionStorage.getItem(key);
};