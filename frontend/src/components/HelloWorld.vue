<template>
	<div class="form-container">
		<el-form
			ref="userFormRef"
			:style="{ maxWidth: '600px', width: '100%' }"
			:label-position="labelPosition"
			:model="userForm"
			:rules="rules"
			label-width="auto"
			class="demo-userForm"
			:size="formSize"
			v-loading="formLoading"
			element-loading-text="加载中..."
		>
			<el-form-item label="姓名" prop="name">
				<el-input v-model="userForm.name" />
			</el-form-item>
			<el-form-item label="性别" prop="gender">
				<el-radio-group v-model="userForm.gender">
					<el-radio value="1" size="large">男</el-radio>
					<el-radio value="0" size="large">女</el-radio>
				</el-radio-group>
			</el-form-item>
			<el-form-item label="年龄" prop="age">
				<el-input-number v-model="userForm.age" :min="1" :max="99" />
			</el-form-item>
			<el-form-item label="住址" prop="address">
				<el-input v-model="userForm.address" :autosize="{ minRows: 2, maxRows: 4 }" type="textarea" placeholder="请输入地址信息" />
			</el-form-item>
			<el-form-item label="爱好" prop="hobby">
				<el-checkbox-group v-model="userForm.hobby">
					<el-checkbox value="singing" name="hobby"> 唱歌 </el-checkbox>
					<el-checkbox value="dancing" name="hobby"> 跳舞 </el-checkbox>
					<el-checkbox value="basketball" name="hobby"> 篮球 </el-checkbox>
				</el-checkbox-group>
			</el-form-item>
			<el-form-item label="备注" prop="desc">
				<el-input v-model="userForm.desc" type="textarea" />
			</el-form-item>
			<el-form-item>
				<el-button type="primary" :loading="loading" @click="submitForm(userFormRef)">
					<el-icon style="vertical-align: middle">
						<Upload />
					</el-icon>
					提交
				</el-button>
				<el-button @click="resetForm(userFormRef)">
					<el-icon style="vertical-align: middle">
						<Refresh />
					</el-icon>
					重置
				</el-button>
				<el-button type="success" @click="getFormData()">
					<el-icon style="vertical-align: middle">
						<Search />
					</el-icon>
					获取数据
				</el-button>
			</el-form-item>
		</el-form>
	</div>
</template>

<script setup>
	import { ref } from 'vue';
	import { ElMessage } from 'element-plus';
	import { post, get } from '@/utils/request';
	import { Upload, Refresh, Search } from '@element-plus/icons-vue';

	const labelPosition = ref('top');
	const formSize = ref('default');
	const userFormRef = ref();
	const loading = ref(false);
	const formLoading = ref(false);
	const userForm = ref({
		name: '',
		gender: '',
		age: 18,
		address: '',
		hobby: [],
		desc: '',
	});

	const rules = ref({
		name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
		gender: [
			{
				required: true,
				message: '请选择性别',
				trigger: 'change',
			},
		],
		age: [
			{
				required: true,
				message: '请输入年龄',
				trigger: 'blur',
			},
		],
		address: [
			{
				required: true,
				message: '请输入地址信息',
				trigger: 'blur',
			},
		],
		hobby: [
			{
				type: 'array',
				required: true,
				message: '请至少选择一个爱好',
				trigger: 'change',
			},
		],
	});

	const submitForm = async formEl => {
		if (!formEl) return;
		await formEl.validate(async (valid, fields) => {
			if (valid) {
				try {
					loading.value = true;
					await post('/userData/submit-form', userForm.value);
					ElMessage.success('提交成功！');
					formEl.resetFields();
				} catch (error) {
					console.error('提交失败：', error);
				} finally {
					loading.value = false;
				}
			} else {
				console.log('error submit!', fields);
				ElMessage.error('请检查表单填写是否正确');
			}
		});
	};

	const resetForm = formEl => {
		if (!formEl) return;
		formEl.resetFields();
	};

	const getFormData = async () => {
		try {
			formLoading.value = true;
			const res = await get('/userData/getUserData', { userId: '666' });
			ElMessage.success('查询成功！');
			userForm.value = res.data;
		} catch (error) {
			console.error('获取数据失败：', error);
			ElMessage.error('获取数据失败');
		} finally {
			formLoading.value = false;
		}
	};
</script>

<style scoped>
	.form-container {
		padding: 20px;
		display: flex;
		justify-content: center;
		min-height: 100vh;
		width: 100%;
	}

	:deep(.el-button) {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		padding: 8px 20px;
		margin-right: 10px;
		transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
		position: relative;
		overflow: hidden;
	}

	:deep(.el-button .el-icon) {
		margin-right: 6px;
		font-size: 16px;
		transition: transform 0.4s ease;
	}

	:deep(.el-button:hover) {
		transform: translateY(-3px);
		box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
	}

	:deep(.el-button:hover .el-icon) {
		transform: scale(1.1);
	}

	:deep(.el-button:active) {
		transform: translateY(0);
		box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	}

	:deep(.el-button--primary) {
		background: linear-gradient(45deg, #409eff, #3a8ee6);
		border: none;
		position: relative;
		overflow: hidden;
	}

	:deep(.el-button--primary::before) {
		content: '';
		position: absolute;
		top: 0;
		left: -100%;
		width: 100%;
		height: 100%;
		background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
		transition: 0.5s;
	}

	:deep(.el-button--primary:hover::before) {
		left: 100%;
	}

	:deep(.el-button--success) {
		background: linear-gradient(45deg, #67c23a, #5daf34);
		border: none;
		position: relative;
		overflow: hidden;
	}

	:deep(.el-button--success::before) {
		content: '';
		position: absolute;
		top: 0;
		left: -100%;
		width: 100%;
		height: 100%;
		background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
		transition: 0.5s;
	}

	:deep(.el-button--success:hover::before) {
		left: 100%;
	}

	:deep(.el-button--primary:hover) {
		background: linear-gradient(45deg, #66b1ff, #409eff);
	}

	:deep(.el-button--success:hover) {
		background: linear-gradient(45deg, #85ce61, #67c23a);
	}

	:deep(.el-loading-mask) {
		background-color: rgba(255, 255, 255, 0.8);
		backdrop-filter: blur(2px);
	}
</style>
