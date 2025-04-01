import axios from 'axios';
import { ElMessage } from 'element-plus';
import crypto from '@/utils/crypto';

// 创建 axios 实例
const service = axios.create({
	baseURL: import.meta.env.VITE_APP_BASE_API,
	timeout: 15000, // 请求超时时间
	headers: {
		'Content-Type': 'application/json;charset=utf-8',
	},
});

// 请求拦截器
service.interceptors.request.use(
	config => {
		const timestamp = Date.now().toString();
		const nonce = crypto.generateNonce();
		// const nonce = 'AaBbCcDdEeFfGg';

		// 判断是否为form-data请求
		const isFormData = config.headers['Content-Type']?.includes('multipart/form-data');
		if (!isFormData) {
			// AES密钥与随机向量
			const aesKey = crypto.generateAESKey();
			const iv = crypto.generateIV();

			// 加密密钥
			const keyData = JSON.stringify({ key: aesKey, iv: iv });
			// console.log('原始密钥数据：', keyData);
			const encryptedAESKey = crypto.rsaEncrypt(keyData);
			// console.log('RSA加密后的密钥：', encryptedAESKey);

			// 验证RSA加密
			const decryptedKey = crypto.rsaDecrypt(encryptedAESKey);
			// console.log('RSA解密后的密钥：', decryptedKey);

			// 比较原始数据和解密后的数据
			const originalKeyData = JSON.parse(keyData);
			// console.log('解析后的原始密钥：', originalKeyData);

			// 直接比较对象，因为 decryptedKey 已经是对象了
			const isKeyValid = decryptedKey.key === originalKeyData.key && decryptedKey.iv === originalKeyData.iv;
			// console.log('RSA加密验证：', isKeyValid);

			// 加密请求体
			const originalData = config.data || {};
			// console.log('原始数据：', originalData);
			const encryptedData = crypto.aesEncrypt(originalData, aesKey, iv);
			// console.log('AES加密后的数据：', encryptedData);

			// 验证AES加密
			const decryptedData = crypto.aesDecrypt(encryptedData, aesKey, iv);
			// console.log('AES解密后的数据：', decryptedData);
			// console.log('AES加密验证：', JSON.stringify(decryptedData) === JSON.stringify(originalData));

			// 生成签名
			const signature = crypto.generateSignature(originalData, timestamp, nonce, aesKey);
			// console.log('生成的签名：', signature);

			// 验证签名
			const isValidSignature = crypto.verifySignature(decryptedData, timestamp, nonce, aesKey, signature);
			// console.log('签名验证：', isValidSignature);

			// 添加请求头
			config.headers['X-Encrypted-Key'] = encryptedAESKey;
			config.headers['X-Signature'] = signature;
			config.headers['X-Timestamp'] = timestamp;
			config.headers['X-Nonce'] = nonce;

			config.data = {
				data: encryptedData,
			};
		}

		return config;
	},
	error => {
		console.error('请求错误：', error);
		return Promise.reject(error);
	}
);

// 响应拦截器
service.interceptors.response.use(
	response => {
		const res = response.data;

		if (res.code === 200) {
			// 获取加密的 AES 密钥
			const encryptedAESKey = response.headers['x-encrypted-key'];
			if (!!encryptedAESKey) {
				// 使用RSA解密AES密钥
				const decryptedKeyStr = crypto.rsaDecrypt(encryptedAESKey);
				// 将解密后的字符串解析为JSON对象
				const decryptedKey = JSON.parse(decodeURIComponent(decryptedKeyStr));
				console.log('响应数据:', res.data);
				const decryptedData = crypto.aesDecrypt(res.data, decryptedKey.key, decryptedKey.iv);
				console.log('AES解密后的数据：', decryptedData);

				try {
					if (typeof decryptedData === 'string') {
						res.data = parseToJSON(decryptedData);
					} else {
						res.data = decryptedData;
					}
				} catch (error) {
					console.error('JSON解析错误：', error);
					throw new Error('响应数据格式错误');
				}
			}
			return res;
		} else {
			ElMessage.error(res.msg || '请求失败');
			return Promise.reject(new Error(res.msg || '请求失败'));
		}
	},
	error => {
		console.error('响应错误：', error);

		let message = '请求失败';
		if (error.response) {
			switch (error.response.status) {
				case 401:
					message = '未授权，请重新登录';
					break;
				case 403:
					message = '拒绝访问';
					break;
				case 404:
					message = '请求错误，未找到该资源';
					break;
				case 500:
					message = '服务器端出错';
					break;
				default:
					message = error.response.data.msg;
			}
		} else if (error.request) {
			message = '网络错误，请检查网络连接';
		}
		ElMessage.error(message);
		return Promise.reject(error);
	}
);

function parseToJSON(str) {
	return JSON.parse(
		str
			.replace(/(\w+)=/g, '"$1":') // 给键加上双引号
			.replace(/:\s*([\w\u4e00-\u9fa5]+)/g, ':"$1"') // 给非数组、非数字的值加引号（包括中文）
			.replace(/:(\d+)/g, ':$1') // 确保数字不被引号包裹
			.replace(/:(\[[^\]]*\])/g, (match, array) => {
				// 处理数组
				return ':' + array.replace(/(\w+)/g, '"$1"');
			})
	);
}

// 封装 GET 请求
export function get(url, params) {
	return service({
		url,
		method: 'get',
		params,
	});
}

// 封装 POST 请求
export function post(url, data) {
	return service({
		url,
		method: 'post',
		data,
	});
}

// 封装 PUT 请求
export function put(url, data) {
	return service({
		url,
		method: 'put',
		data,
	});
}

// 封装 DELETE 请求
export function del(url, params) {
	return service({
		url,
		method: 'delete',
		params,
	});
}

export default service;
