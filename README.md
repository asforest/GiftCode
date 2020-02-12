# GiftCode
gift code plugin for nukkit


## 更新记录

#### 1.3:
1. 优化部分文本说明，以及优化部分代码逻辑
2. 移除"删除所有礼包码"按钮，避免误触导致误删
3. 新增礼包码填充，使用/code asdasd，后面跟带具体礼包码时会直接填充到文本框里，避免再打一次
4. 调整"设置参数"按钮为"设置参数并重新生成"
5. 新增字符池(config.yml里)，生成礼包码时会随机读取里面字符(推荐只写小写字母和数字,大写不易区分)

#### 1.4:
1. 修复新建礼包码时默认礼包选择位置的问题
2. 新增生成礼包码时指定格式的功能
3. 新增礼包码使用情况概览面板
4. 修改新创建的礼包码默认长度为8位
5. 修改礼包码配置文件名"giftCodes.yml" -> "gift-codes.yml",启动时插件会自动转换
6. 修复一个潜在的创建礼包码时的空指针问题
7. 规范API命名中的语法单词问题
8. 新增指定礼包码格式功能(参数面板中,仅限公用礼包码),*号代表随机填充的字符