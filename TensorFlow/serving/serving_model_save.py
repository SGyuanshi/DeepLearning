"""
将计算图以pb格式进行保存，用于tf-serving
"""
import tensorflow.compat.v1 as tf
# import tensorflow as tf

# tf2，否则placeholde会报错
tf.disable_eager_execution()

############# 在这里定义你的模型  ###########
x1 = tf.placeholder(tf.float32, [None], name='x1')
inputs_id = tf.placeholder(tf.int32, [None], name='x2')

out = tf.add(tf.multiply(x1, 0.5), 2)

embedding = tf.get_variable("embedding_table", shape=[100, 10])
pre = tf.nn.embedding_lookup(embedding, inputs_id)
############# 在这里定义你的模型  ###########

sess = tf.Session()
sess.run(tf.global_variables_initializer())

# 将张量转化为tensor_info
tensor_info_x1 = tf.saved_model.utils.build_tensor_info(x1)
tensor_info_inputs_id = tf.saved_model.utils.build_tensor_info(inputs_id)
tensor_info_out = tf.saved_model.utils.build_tensor_info(out)
tensor_info_pre = tf.saved_model.utils.build_tensor_info(pre)

# 创建SavedModelBuilder，指定保存路径
builder = tf.saved_model.builder.SavedModelBuilder("serving-model/1")
# 定义签名，在这里指定接口的输入以及返回
# 接口传参：{"instances": [{"x1": [1.0, 2.0, 5.0],"inputs_id": [1, 2, 3]}]}
# 必须将输入参数传递给"instances"，否则接口不通过
# 返回：{"predictions":[{"out":......., "pre":......}]}
prediction_signature = (
  tf.saved_model.signature_def_utils.build_signature_def(
      inputs={'x1': tensor_info_x1, "inputs_id": tensor_info_inputs_id},
      outputs={'out': tensor_info_out, "pre": tensor_info_pre},
      method_name=tf.saved_model.signature_constants.PREDICT_METHOD_NAME))
# 模型保存
legacy_init_op = tf.group(tf.tables_initializer(), name='legacy_init_op')
builder.add_meta_graph_and_variables(
    sess, [tf.saved_model.tag_constants.SERVING],
    signature_def_map={
        # 'predict_images':
        #     prediction_signature,
        # tf.saved_model.signature_constants.DEFAULT_SERVING_SIGNATURE_DEF_KEY:
        #     classification_signature,
        tf.saved_model.signature_constants.DEFAULT_SERVING_SIGNATURE_DEF_KEY:
            prediction_signature,
    },
    legacy_init_op=legacy_init_op)

builder.save()

print('Done exporting!')