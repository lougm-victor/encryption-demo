import CryptoJS from 'crypto-js';
import JSEncrypt from 'jsencrypt';

const PUBLIC_KEY = `
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxP0394Nq5WxMfJ9kVH9o
0lhSnQ79+LU+o21/ZFXBsISScpqDDS57NZkWsI5cIXZnjwWGnliYiiLF8mhUgtVf
CBgWYEcjOj1eGmnZVakIKPJ7nCvN5JlOfKAQnamOevW3rjm+Li6D6b4afQJ35Pek
c9vDrSoY0OflL89OxG8o1MHmG3FYS9nZP48uXc6eY0KleljJ+1qTWbA06hxXjPNw
t1UOWnfpb6cuBuHpUB5S5ayMc7lZfpSyyEVZkUjSbVy++LTL+rK0H6SbrpaswDHv
BPKcEot9eZhkCTteIx+MkY19aPtaURqOOZ7GKCQ5/P3H89gysCsS8PvcP9P80gdm
8QIDAQAB`;

const PRIVATE_KEY = `
MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDE/Tf3g2rlbEx8
n2RUf2jSWFKdDv34tT6jbX9kVcGwhJJymoMNLns1mRawjlwhdmePBYaeWJiKIsXy
aFSC1V8IGBZgRyM6PV4aadlVqQgo8nucK83kmU58oBCdqY569beuOb4uLoPpvhp9
Anfk96Rz28OtKhjQ5+Uvz07EbyjUweYbcVhL2dk/jy5dzp5jQqV6WMn7WpNZsDTq
HFeM83C3VQ5ad+lvpy4G4elQHlLlrIxzuVl+lLLIRVmRSNJtXL74tMv6srQfpJuu
lqzAMe8E8pwSi315mGQJO14jH4yRjX1o+1pRGo45nsYoJDn8/cfz2DKwKxLw+9w/
0/zSB2bxAgMBAAECggEAaaG7jD9tZ/8t+LnkLCdyRzFTg6MRb3eJUkuaP2x5A7Xj
bg8K7VuaeEBSWXTNVKhQmfIFoQ2zJofFC9sETnAqmIWKIjCjZEyaFKHkCDovBP4a
aMFYlBfdbZjLsC/HFoqC56tRHySUWhg1zgkvz5k4F2SmRkpHqmZfkMai31UYWRV6
RIWtzDdogfy+RSYEs78S/rUXx/LUkZd1Tdfmppu4GWajIiUKPyJLnauGYmPexJB5
pOW4IysVBv3J9cZKPsEAzwXboPPznKoyBY69Sg07EgiLPbB9HY9Q/+BvKPDwbIRe
ymzq7qrp5+rr8tah/1AbxtkgblxG2GaH4FyZa6DGAQKBgQD9HwKA16tJ0U+mCTPG
bSUpdo8ExVU+cFhxYJF1909eZoieNfJf4lwNPPT9LwYb+sMCiykK9bNl86+F4qA2
t5zBimyZ+g1Enm8AOXDdrUmP68QJ5EZblVowjrPXDlRZdZcz89/YdbRdFkniNqfd
cxUVqgCrsmQwNL0zDsh3dj81IQKBgQDHOsY5dAq+iWro9eHRWyVWyt+grcf+Audu
ENYiTcbBWsQHH49CYYCTkj3WpdiL8H2NKXObAiiT56wLh1IEWDNHK/ztSB4krk8t
n5Tvdl3mKoO9Wrq+EGnF5Wl8a9X2qK+cOcbyJqZBUXOAPeVtZxwrEO+X5ShHPCam
K1Z3N7Un0QKBgCA2Fgd3aqDWNiCXAzO5KAWCxOiChDqR6W9kLp6ofj0xJymbC+EP
2U8F9tdRaKGe47orI2n1ooLS4/9Ru7m63AP1LaTaoV2eG9oXdTmAl9/szOHFXv7A
nt2XC2POuwU3ZaqC2nWdZPqDn5teATa5MnGzVKqE5SMNUxZwRyqMrbaBAoGAezpH
UVINMz0b+2vaePu/ZKenGmNiyCJTsyiGykEBJqdViHJk0dItDRnvVczO2/+sKJvy
/XWgU5uANsFEYZuMXAFBOCLbQiTupHGIVXUJL0wsn/p2dwj/43+6d1LbzO0Zca/q
9ewh+VS/9BpUxg54xcp755++AhGadj1oBeVXD4ECgYEAw3af6RsrVgxwzUR3/DIo
n8+xPk3pmevEnbvjLzfPaSy7viiOwr2GmrctqcsMLOz96sWSvTqzbeg1rEg6cK/H
T3sQK2xShrDe+qdCree6+Y/AKGPc2VV5/RXBVZ7SKUBUrECMj882tOkxOFWB88NZ
Cm+KQEbfETTr5Lc2oRwpy6I=`;

const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

export default {
	/**
	 * 生成随机的AES密钥
	 *
	 * @param {number} length 密钥长度（默认为16字节，对应128位）
	 * @returns {string} Base64编码的密钥
	 */
	generateAESKey(length = 16) {
		let result = '';
		const charactersLength = characters.length;
		for (let i = 0; i < length; i++) {
			result += characters.charAt(Math.floor(Math.random() * charactersLength));
		}

		return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(result));
	},

	/**
	 * 生成随机的初始化向量
	 *
	 * @param {number} length 向量长度（必须为16字节）
	 * @returns {string} Base64编码的初始化向量
	 */
	generateIV(length = 16) {
		let result = '';
		const charactersLength = characters.length;
		for (let i = 0; i < length; i++) {
			result += characters.charAt(Math.floor(Math.random() * charactersLength));
		}

		return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(result));
	},

	/**
	 * RSA加密
	 *
	 * @param {string} data 待加密数据
	 * @returns {string} 加密后的数据
	 */
	rsaEncrypt(data) {
		const encrypt = new JSEncrypt();
		encrypt.setPublicKey(PUBLIC_KEY);

		const maxLength = 245;
		if (data.length <= maxLength) {
			return encrypt.encrypt(data);
		}

		const chunks = [];
		for (let i = 0; i < data.length; i += maxLength) {
			const chunk = data.slice(i, i + maxLength);
			const encryptedChunk = encrypt.encrypt(chunk);
			if (!encryptedChunk) {
				throw new Error('RSA encryption failed');
			}
			chunks.push(encryptedChunk);
		}

		return chunks.join('@~@');
	},

	/**
	 * RSA解密
	 *
	 * @param {string} encryptedData 加密数据
	 * @returns {Object|string} 解密后的数据
	 */
	rsaDecrypt(encryptedData) {
		const decrypt = new JSEncrypt();
		decrypt.setPrivateKey(PRIVATE_KEY);

		if (encryptedData.includes('@~@')) {
			const chunks = encryptedData.split('@~@');
			let decrypted = '';

			for (const chunk of chunks) {
				const decryptedChunk = decrypt.decrypt(chunk);
				if (!decryptedChunk) {
					throw new Error('RSA decryption failed');
				}
				decrypted += decryptedChunk;
			}

			try {
				return JSON.parse(decrypted);
			} catch (e) {
				return decrypted;
			}
		}

		const decrypted = decrypt.decrypt(encryptedData);
		try {
			return JSON.parse(decrypted);
		} catch (e) {
			return decrypted;
		}
	},

	/**
	 * AES加密
	 *
	 * @param {Object|string} data 待加密数据
	 * @param {string} key AES密钥
	 * @param {string} iv 向量
	 * @returns {string} 加密后的数据
	 */
	aesEncrypt(data, key, iv) {
		const dataStr = typeof data === 'object' ? JSON.stringify(data) : String(data);

		const keyBytes = CryptoJS.enc.Base64.parse(key);
		const ivBytes = CryptoJS.enc.Base64.parse(iv);

		const encrypted = CryptoJS.AES.encrypt(dataStr, keyBytes, {
			iv: ivBytes,
			mode: CryptoJS.mode.CBC,
			padding: CryptoJS.pad.Pkcs7,
		});

		return encrypted.toString();
	},

	/**
	 * AES解密
	 *
	 * @param {string} encryptedData 加密数据
	 * @param {string} key AES密钥
	 * @param {string} iv 向量
	 * @returns {Object|string} 解密后的数据
	 */
	aesDecrypt(encryptedData, key, iv) {
		const keyParse = CryptoJS.enc.Base64.parse(key);
		const ivParse = CryptoJS.enc.Base64.parse(iv);

		const decrypted = CryptoJS.AES.decrypt(encryptedData, keyParse, {
			iv: ivParse,
			mode: CryptoJS.mode.CBC,
			padding: CryptoJS.pad.Pkcs7,
		});

		const decryptedStr = decrypted.toString(CryptoJS.enc.Utf8);

		try {
			return JSON.parse(decryptedStr);
		} catch (e) {
			return decryptedStr;
		}
	},

	/**
	 * 生成签名
	 *
	 * @param {Object} params 请求参数
	 * @param {string} timestamp 时间戳
	 * @param {string} nonce 随机字符串
	 * @param {string} aesKey AES密钥
	 * @returns {string} 签名
	 */
	generateSignature(params, timestamp, nonce, aesKey) {
		// 1. 对参数按key进行字典排序
		const sortedKeys = Object.keys(params).sort();
		const sortedParams = {};
		sortedKeys.forEach(key => {
			sortedParams[key] = params[key];
		});

		// 2. 拼接参数字符串
		const paramStr = JSON.stringify(sortedParams);

		// 3. 组合签名原文
		const signStr = [`params=${paramStr}`, `timestamp=${timestamp}`, `nonce=${nonce}`, `aesKey=${aesKey}`, `version=1.0`].sort().join('&');

		// 4. 多重哈希
		let hash = CryptoJS.SHA512(signStr).toString();
		hash = CryptoJS.SHA256(hash).toString();
		hash = CryptoJS.MD5(hash).toString();

		return hash;
	},

	/**
	 * 验证签名
	 *
	 * @param {Object} params 请求参数
	 * @param {string} timestamp 时间戳
	 * @param {string} nonce 随机字符串
	 * @param {string} aesKey AES密钥
	 * @param {string} signature 签名
	 * @returns {boolean} 验证结果
	 */
	verifySignature(params, timestamp, nonce, aesKey, signature) {
		// 1. 对参数按key进行字典排序
		const sortedKeys = Object.keys(params).sort();
		const sortedParams = {};
		sortedKeys.forEach(key => {
			sortedParams[key] = params[key];
		});

		// 2. 拼接参数字符串
		const paramStr = JSON.stringify(sortedParams);

		// 3. 组合签名原文
		const signStr = [`params=${paramStr}`, `timestamp=${timestamp}`, `nonce=${nonce}`, `aesKey=${aesKey}`, `version=1.0`].sort().join('&');

		// 4. 多重哈希
		let hash = CryptoJS.SHA512(signStr).toString();
		hash = CryptoJS.SHA256(hash).toString();
		hash = CryptoJS.MD5(hash).toString();

		return hash === signature;
	},

	/**
	 * 生成随机字符串
	 *
	 * @param {number} length 长度
	 * @returns {string} 随机字符串
	 */
	generateNonce(length = 32) {
		const array = new Uint8Array(length);
		const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
		let result = '';

		for (let i = 0; i < length; i++) {
			const timestamp = Date.now();
			const random = Math.random();
			const combined = timestamp * random;
			array[i] = Math.floor(combined % chars.length);
			result += chars.charAt(array[i]);
		}

		const timePrefix = Date.now().toString(36).slice(-3);
		result = timePrefix + result.slice(0, length - 3);

		return result;
	},
};
