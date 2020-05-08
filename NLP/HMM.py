import numpy as np


class HMM:
    """
    状态取值有N个，观测序列取值有M个
    """
    def __init__(self, A: np.ndarray, B: np.ndarray, pi):
        self.A = A  # 状态转移概率矩阵 [N, N]
        self.B = B  # 输出观测概率矩阵 [N, M]
        self.pi = pi  # 初设状态概率向量 [N]

        self.N = len(pi)
        self.M = self.A.shape[1]

        self.map_i = {}  # 存储状态取值对应的编码, key为编码，value为状态值
        self.map_o = {}  # 存储观测值对应的编码， key为观测值，value为编码

    def init_map(self, I, O):
        """
        将观测值和状态值从文本映射到0,1,.....的整数
        :param I:
        :param O:
        :return:
        """
        index = 0
        map_i = {}
        for ii in I:
            for i in ii:
                if i not in map_i.keys():
                    map_i[i] = index
                    index += 1
        # 对map进行k-v反转
        for k, v in map_i.items():
            self.map_i[v] = k

        index = 0
        for oo in O:
            for o in oo:
                if o not in self.map_o.keys():
                    self.map_o[o] = index
                    index += 1

    def forward(self, t, i, O):
        """
        前向算法
        :param t: 时刻t，以0为起点
        :param i: 状态值，[0, 1, ...., N-1]
        :param O: 观测序列
        :return:
        """
        if t == 0:
            return self.pi[i] * self.B[i][O[t]]

        return self.B[i][O[t]] * sum([self.forward(t - 1, j, O) * self.A[j][i] for j in range(self.N)])

    def backward(self, t, i, O):
        """
        后向算法
        :param t: 时刻t，以0为起点
        :param i: 状态值，[0, 1, ...., N-1]
        :param O: 观测序列
        :return:
        """
        if t == len(O) - 1:
            return 1

        return sum([self.A[i][j] * self.B[j][O[t+1]] * self.backward(t+1, j, O) for j in range(self.N)])

    def gamma(self, t, i, O):
        """
        根据已知隐马尔科夫模型的参数和观测序列O，时刻t处于状态i的概率
        :param t: 时刻t，以0为起点
        :param i: 状态值，[0, 1, ...., N-1]
        :param O: 观测序列
        :return:
        """
        molecule = self.forward(t, i, O) * self.backward(t, i, O)
        denominator = sum([self.forward(t, j, O) * self.backward(t, j, O)] for j in range(self.N))
        return molecule / denominator

    def xi(self, t, i, j, O):
        """
        根据已知隐马尔科夫模型的参数和观测序列O，时刻t处于状态i且时刻t+1处于状态j的概率
        :param t: 时刻t，以0为起点
        :param i: 状态值，[0, 1, ...., N-1]
        :param O: 观测序列
        :return:
        """
        molecule = self.forward(t, i, O) * self.A[i][j] * self.B[j][O[t+1]] * self.backward(t+1, j, O)
        denominator = 0
        for n in range(self.N):
            for m in range(self.N):
                denominator += self.forward(t, n, O) * self.A[n][m] * self.B[m][O[t+1]] * self.backward(t+1, m, O)

        return molecule / denominator

    def gamma(self, t, i, O):
        """
        根据已知隐马尔科夫模型的参数和观测序列O，时刻t处于状态i的概率
        :param t: 时刻t，以0为起点
        :param i: 状态值，[0, 1, ...., N-1]
        :param O: 观测序列
        :return:
        """
        molecule = self.forward(t, i, O) * self.backward(t, i, O)
        denominator = sum([self.forward(t, j, O) * self.backward(t, j, O)] for j in range(self.N))
        return molecule / denominator

    def xi(self, t, i, j, O):
        """
        根据已知隐马尔科夫模型的参数和观测序列O，时刻t处于状态i且时刻t+1处于状态j的概率
        :param t: 时刻t，以0为起点
        :param i: 状态值，[0, 1, ...., N-1]
        :param O: 观测序列
        :return:
        """
        molecule = self.forward(t, i, O) * self.A[i][j] * self.B[j][O[t+1]] * self.backward(t+1, j, O)
        denominator = 0
        for n in range(self.N):
            for m in range(self.N):
                denominator += self.forward(t, n, O) * self.A[n][m] * self.B[m][O[t+1]] * self.backward(t+1, m, O)

        return molecule / denominator

    def train(self, O):
        """
        根据隐马尔科夫模型参数的估计值，极大化模型参数
        :param O:
        :return:
        """
        pi = self.pi
        A = self.A
        B = self.B

        for i in range(self.N):
            pi[i] = self.gamma(0, i, O)

            # 根据公式计算a(i,j)
            for j in range(self.N):
                A[i][j] = sum([self.xi(t, i, j, O) for t in range(len(O)-1)]
                              ) / sum([self.gamma(t, i, O) for t in range(len(O)-1)])

            # 根据公式计算b_i(k)
            B_denominator = sum([self.gamma(t, i, O) for t in range(len(O))])  # 公式中的分母
            # 求得观测序列的所有不同的取值，和对应的所有时刻。
            # 比如观测序列(1, 2, 1)，第一个和最后一个时刻的取值为1，第二个时刻的取值为2
            Map = {}
            for t in range(len(O)):
                if O[t] in Map.keys():
                    Map[O[t]].append(t)
                Map[O[t]] = [t]
            # 这里k对应观测序列中的一个不同的值
            for k, tl in Map.items():
                B[i][k] = sum([self.gamma(t, i, O) for t in tl]) / B_denominator

        # 更新模型参数
        self.pi = pi
        self.A = A
        self.B = B

    def run(self, train_nums, O):
        """
        对训练样本进行训练
        :param train_nums: 训练轮数
        :param O: 所有训练样本，二维数组
        :return:
        """
        for i in range(train_nums):
            for o in O:
                # 将观测文本值映射为对应编码
                o = [self.map_o[m] for m in o]
                self.train(o)  # 对一个观测序列训练，进行参数更新

    def viterbi(self, O):
        """
        维特比编码求最优路径
        :param O:
        :return:
        """
        pro = np.ones([len(O), self.N])  # 记录累计概率
        backpointers = np.ones([len(O), self.N], dtype=np.int32)  # 记录最优路径

        # 计算时刻t=1，各个状态下观测序列为o_1的概率
        pro[0] = [self.pi[i] * self.B[i][O[0]] for i in range(self.N)]
        # 时刻t=2,3,....,
        for t in range(1, len(O)):
            b = [self.B[i][O[t]] for i in range(self.N)]  # 计算各个状态下观测序列为o_t的概率
            v = np.expand_dims(pro[t - 1], 1) * self.A  # 计算前一个时刻状态为j，当前时刻状态为i的概率矩阵
            pro[t] = np.max(v, 0) * b  # 当前状态为i的最大概率 * 对应状态下观测序列为o_t的概率
            backpointers[t] = np.argmax(v, 0)  # 记录，当前状态为i的概率最大时，上个时刻的状态

        # 开始回溯
        viterbi = [np.argmax(pro[-1])]  # 最终累计概率最大时，最后一个时刻的状态
        # 根据当前状态从最优路径中挑选上个时刻的状态
        for bp in reversed(backpointers[1:]):
            viterbi.append(bp[viterbi[-1]])
        viterbi.reverse()

        # 将状态编码转化为对应的状态值
        # viterbi = [self.map_i[v] for v in viterbi]

        viterbi_score = np.max(pro[-1])  # 最高的累计概率
        return viterbi, viterbi_score


if __name__ == '__main__':
    A = np.array([[0.5, 0.2, 0.3],
                  [0.3, 0.5, 0.2],
                  [0.2, 0.3, 0.5]])
    B = np.array([[0.5, 0.5],
                  [0.4, 0.6],
                  [0.7, 0.3]])
    pi = [0.2, 0.4, 0.4]
    O = [0, 1, 0]
    hmm = HMM(A, B, pi)
    print(hmm.forward(t=2, i=0, O=O))

    v, s = hmm.viterbi(O)
    print(v, s)