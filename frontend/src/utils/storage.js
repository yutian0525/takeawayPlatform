// --- sessionStorage 操作 ---

/**
 * 设置 sessionStorage
 * @param {string} key 键名
 * @param {*} value 键值，如果是对象会自动进行 JSON.stringify
 */
export function setSession(key, value) {
    if (typeof value === 'object') {
        sessionStorage.setItem(key, JSON.stringify(value));
    } else {
        sessionStorage.setItem(key, value);
    }
}

/**
 * 获取 sessionStorage
 * @param {string} key 键名
 * @returns {*} 键值，如果是 JSON 字符串会自动进行 JSON.parse
 */
export function getSession(key) {
    const value = sessionStorage.getItem(key);
    try {
        return JSON.parse(value);
    } catch (e) {
        return value;
    }
}

/**
 * 移除 sessionStorage 中的指定项
 * @param {string} key 键名
 */
export function removeSession(key) {
    sessionStorage.removeItem(key);
}

/**
 * 清空所有 sessionStorage
 */
export function clearSession() {
    sessionStorage.clear();
}

// --- localStorage 操作 ---

/**
 * 设置 localStorage
 * @param {string} key 键名
 * @param {*} value 键值，如果是对象会自动进行 JSON.stringify
 */
export function setLocal(key, value) {
    if (typeof value === 'object') {
        localStorage.setItem(key, JSON.stringify(value));
    } else {
        localStorage.setItem(key, value);
    }
}

/**
 * 获取 localStorage
 * @param {string} key 键名
 * @returns {*} 键值，如果是 JSON 字符串会自动进行 JSON.parse
 */
export function getLocal(key) {
    const value = localStorage.getItem(key);
    try {
        return JSON.parse(value);
    } catch (e) {
        return value;
    }
}

/**
 * 移除 localStorage 中的指定项
 * @param {string} key 键名
 */
export function removeLocal(key) {
    localStorage.removeItem(key);
}

/**
 * 清空所有 localStorage
 */
export function clearLocal() {
    localStorage.clear();
}