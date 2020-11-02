import numpy as np
import tensorflow as tf


class MyRnnCell(tf.nn.rnn_cell.RNNCell):

    def __init__(self, state_size, output_size):
        """Initialize My RnnCell.

        Args:
          transition_params: A [num_tags, num_tags] matrix of binary potentials.
              This matrix is expanded into a [1, num_tags, num_tags] in preparation
              for the broadcast summation occurring within the cell.
        """
        self._state_size = state_size
        self._output_size = output_size

    @property
    def state_size(self):
        return self._state_size

    @property
    def output_size(self):
        return self._output_size

    def __call__(self, inputs, state, scope=None):
        """Build My RnnCell.

        Args:
          inputs: A [batch_size, dims] matrix. 当前时刻的输入
          state: A [batch_size, state_size] matrix. 上一个时刻的state
          scope: Unused variable scope of this cell.

        Returns:
          output：A [batch_size, output_size] matrix.当前时刻的输出
          new_alphas: A [batch_size, state_size] matrix.当前时刻的state.
        """
        """在这里，自定义你的计算"""
        output = tf.layers.dense(inputs, self._output_size) + tf.layers.dense(state, self._output_size)

        new_alphas = tf.layers.dense(inputs, self._state_size) + state

        return output, new_alphas


def rnn_test(inputs, sequence_lengths, state_size, output_size):

    batch_size = inputs.get_shape().as_list()[0]

    my_cell = MyRnnCell(state_size, output_size)

    initial_state = tf.zeros([batch_size, state_size])

    # output: [batch_size, max_seq_len, output_size]，所有时刻的输出
    # state: [batch_size, state_size]，最后一个时刻的state
    output, state = tf.nn.dynamic_rnn(
        cell=my_cell,
        inputs=inputs,
        sequence_length=sequence_lengths,
        initial_state=initial_state,
        dtype=tf.float32)

    return output, state


if __name__ == '__main__':
    inputs_arr = np.random.random([20, 10, 5])
    sequence_lengths_arr = np.random.randint(0, 10, [20])

    inputs = tf.placeholder(tf.float32, [None, 10, 5])
    transition_params = tf.placeholder(tf.float32, [5, 5])
    sequence_lengths = tf.placeholder(tf.int64, [None])

    feed_dict = {inputs: inputs_arr, sequence_lengths: sequence_lengths_arr}

    sess = tf.Session()
    output, state = rnn_test(inputs, sequence_lengths, 30, 60)
    print(sess.run([output, state], feed_dict=feed_dict))
