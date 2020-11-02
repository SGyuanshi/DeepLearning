import tensorflow as tf
import numpy as np
from tensorflow.contrib.crf import crf_log_likelihood


class BiLstmCrf:

    def crf_log_likelihood(self,
                           inputs,
                           tag_indices,
                           sequence_lengths,
                           transition_params=None):
        """Computes the log-likelihood of tag sequences in a CRF.

        Args:
          inputs: A [batch_size, max_seq_len, num_tags] tensor of unary potentials
              to use as input to the CRF layer.
          tag_indices: A [batch_size, max_seq_len] matrix of tag indices for which we
              compute the log-likelihood.
          sequence_lengths: A [batch_size] vector of true sequence lengths.
          transition_params: A [num_tags, num_tags] transition matrix, if available.
        Returns:
          log_likelihood: A [batch_size] `Tensor` containing the log-likelihood of
            each example, given the sequence of tag indices.
          transition_params: A [num_tags, num_tags] transition matrix. This is either
              provided by the caller or created in this function.
        """
        max_seq_len = inputs.get_shape().as_list()[1]
        num_tags = inputs.get_shape().as_list()[2]

        # LSTM的发射矩阵的累计得分
        mask = tf.sequence_mask(sequence_lengths, max_seq_len, dtype=tf.float32)
        inputs = inputs * tf.expand_dims(mask, axis=-1)
        sequence_score = inputs * tf.one_hot(tag_indices, depth=num_tags, dtype=tf.float32)
        sequence_score = tf.reduce_sum(tf.reduce_sum(sequence_score, axis=-1), axis=-1)

        # 转移概率累计得分
        transition_score = tf.gather(transition_params, axis=0, indices=tag_indices[:, :-1]) * tf.expand_dims(
            mask[:, 1:], axis=-1) * tf.one_hot(tag_indices[:, 1:], depth=num_tags, dtype=tf.float32)
        transition_score = tf.reduce_sum(tf.reduce_sum(transition_score, axis=-1), axis=-1)

        # 归一化因子计算
        alpha = [inputs[:, 0, n:(n + 1)] for n in range(num_tags)]  # [batch_size, num_tags]
        for t in range(1, max_seq_len):
            temp = alpha.copy()
            for n in range(num_tags):
                alpha[n] = tf.where(tf.equal(mask[:, t:(t + 1)], 1), tf.reduce_logsumexp(
                    tf.concat(temp, axis=-1) + tf.transpose(transition_params[:, n:(n + 1)]), axis=-1,
                    keepdims=True) + inputs[:, t, n:(n + 1)], temp[n])

        log_norm = tf.reduce_logsumexp(tf.concat(alpha, axis=-1), axis=-1)

        return log_norm - (sequence_score + transition_score)


if __name__ == '__main__':
    inputs_arr = np.random.random([20, 10, 5])
    tag_indices_arr = np.random.randint(0, 5, [20, 10])
    transition_params_arr = np.random.random([5, 5])
    sequence_lengths_arr = np.random.randint(0, 10, [20])

    inputs = tf.placeholder(tf.float32, [None, 10, 5])
    tag_indices = tf.placeholder(tf.int64, [None, 10])
    transition_params = tf.placeholder(tf.float32, [5, 5])
    # sequence_lengths = np.full([100], 10)
    sequence_lengths = tf.placeholder(tf.int64, [None])

    sess = tf.Session()

    crf = BiLstmCrf()
    res1 = crf.crf_log_likelihood(inputs,
                                  tag_indices,
                                  sequence_lengths,
                                  transition_params)
    res2 = crf_log_likelihood(inputs,
                              tag_indices,
                              sequence_lengths,
                              transition_params)
    feed_dict = {inputs: inputs_arr, tag_indices: tag_indices_arr, sequence_lengths: sequence_lengths_arr,
                 transition_params: transition_params_arr}
    print(sess.run(res1, feed_dict=feed_dict))
    print(sess.run(res2, feed_dict=feed_dict))
